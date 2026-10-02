package be.eliasb.config;

import be.eliasb.Cinder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;

public final class CinderConfig {
  public static final CinderConfig INSTANCE = new CinderConfig();

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("cinder.json");
  private static final Object IO_LOCK = new Object();

  public boolean enableFrustumCulling = true;
  public boolean enableOcclusionCulling = true;
  public int maxParticleDistance = 32;
  public int maxParticlesPerTick = 1500;

  public boolean enableDensityThrottling = true;
  public int maxDensityDropPercent = 75;
  public boolean optimizePhysics = true;
  public boolean smoothFading = true;
  public boolean cacheLightPerTick = true;

  public int frustumMarginDegrees = 20;
  public int physicsDisableDistance = 16;
  public int occlusionMinDistance = 6;

  public boolean useRelativeDistance = false;
  public int relativeDistancePercent = 100;

  public Map<String, Float> particleTypeOverrides = new LinkedHashMap<>();

  public int maxOcclusionRaycastsPerTick = 200;

  public Set<String> particleTypeNeverCull = new LinkedHashSet<>();

  private CinderConfig() {}

  public static void load() {
    synchronized (IO_LOCK) {
      if (Files.exists(FILE)) {
        try {
          var json = Files.readString(FILE);
          var data = GSON.fromJson(json, CinderConfig.class);
          if (data != null) {
            INSTANCE.copyFrom(data);
          }
        } catch (IOException | RuntimeException e) {
          Cinder.LOGGER.warn("[Cinder] Failed to read config, using defaults", e);
        }
      }
      INSTANCE.sanitize();
    }
    save();
  }

  public static void save() {
    INSTANCE.sanitize();
    final String json = GSON.toJson(INSTANCE);
    Thread.startVirtualThread(
            () -> {
              synchronized (IO_LOCK) {
                try {
                  Files.writeString(FILE, json);
                } catch (IOException e) {
                  Cinder.LOGGER.warn("[Cinder] Failed to save config", e);
                }
              }
            });
  }

  public float getSpawnChance(String particleTypeId) {
    Float v = particleTypeOverrides.get(particleTypeId);
    return v == null ? 1.0f : v;
  }

  private void copyFrom(CinderConfig o) {
    enableFrustumCulling = o.enableFrustumCulling;
    enableOcclusionCulling = o.enableOcclusionCulling;
    maxParticleDistance = o.maxParticleDistance;
    maxParticlesPerTick = o.maxParticlesPerTick;
    enableDensityThrottling = o.enableDensityThrottling;
    maxDensityDropPercent = o.maxDensityDropPercent;
    optimizePhysics = o.optimizePhysics;
    smoothFading = o.smoothFading;
    frustumMarginDegrees = o.frustumMarginDegrees;
    physicsDisableDistance = o.physicsDisableDistance;
    occlusionMinDistance = o.occlusionMinDistance;
    cacheLightPerTick = o.cacheLightPerTick;
    useRelativeDistance = o.useRelativeDistance;
    relativeDistancePercent = o.relativeDistancePercent;
    maxOcclusionRaycastsPerTick = o.maxOcclusionRaycastsPerTick;
    if (o.particleTypeOverrides != null) {
      particleTypeOverrides = new LinkedHashMap<>(o.particleTypeOverrides);
    }
    if (o.particleTypeNeverCull != null) {
      particleTypeNeverCull = new LinkedHashSet<>(o.particleTypeNeverCull);
    }
  }

  private void sanitize() {
    maxParticleDistance = clamp(maxParticleDistance, 8, 256);
    maxParticlesPerTick = clamp(maxParticlesPerTick, 50, 20000);
    maxDensityDropPercent = clamp(maxDensityDropPercent, 0, 100);
    frustumMarginDegrees = clamp(frustumMarginDegrees, 0, 90);
    physicsDisableDistance = clamp(physicsDisableDistance, 4, 128);
    occlusionMinDistance = clamp(occlusionMinDistance, 2, 64);
    relativeDistancePercent = clamp(relativeDistancePercent, 10, 400);
    maxOcclusionRaycastsPerTick = clamp(maxOcclusionRaycastsPerTick, 10, 5000);

    if (particleTypeOverrides == null) {
      particleTypeOverrides = new LinkedHashMap<>();
    } else {
      particleTypeOverrides.replaceAll(
              (id, chance) -> {
                float c = (chance == null || chance.isNaN()) ? 1.0f : chance;
                return Math.max(0.0f, Math.min(1.0f, c));
              });
    }

    if (particleTypeNeverCull == null) {
      particleTypeNeverCull = new LinkedHashSet<>();
    }
  }

  private static int clamp(int v, int lo, int hi) {
    return Math.max(lo, Math.min(hi, v));
  }
}