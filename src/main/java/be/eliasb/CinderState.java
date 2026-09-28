package be.eliasb;

import be.eliasb.config.CinderConfig;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class CinderState {
  private static final double ASSUMED_ASPECT = 21.0 / 9.0;
  private static final int OCCLUSION_CACHE_LIMIT = 8192;

  public static boolean active;
  public static boolean frustumUsable;

  public static double camX, camY, camZ;
  public static double lookX, lookY, lookZ;
  public static double cosFrustum = -1.0;

  public static Vec3 eye;
  public static Entity viewer;

  public static int spawnedThisTick;
  public static int currentTick;

  public static double effectiveMaxDistance = 32.0;
  public static final Long2ByteOpenHashMap OCCLUSION = new Long2ByteOpenHashMap();

  static {
    OCCLUSION.defaultReturnValue((byte) -1);
  }

  private CinderState() {}

  public static void beginTick(Minecraft mc) {
    currentTick++;
    spawnedThisTick = 0;
    if (!OCCLUSION.isEmpty()) {
      OCCLUSION.clear();
    }

    var cfg = CinderConfig.INSTANCE;
    if (cfg.useRelativeDistance) {
      int renderDistanceChunks = mc.options.renderDistance().get();
      effectiveMaxDistance = renderDistanceChunks * 16.0 * (cfg.relativeDistancePercent / 100.0);
    } else {
      effectiveMaxDistance = cfg.maxParticleDistance;
    }

    Entity cam = mc.getCameraEntity();
    if (mc.level == null || cam == null) {
      active = false;
      return;
    }

    viewer = cam;
    eye = cam.getEyePosition();
    camX = eye.x;
    camY = eye.y;
    camZ = eye.z;

    Vec3 look = cam.getViewVector(1.0F);
    lookX = look.x;
    lookY = look.y;
    lookZ = look.z;

    // In third person the camera looks along a different axis than the entity, so the cone would be
    // wrong.
    frustumUsable = mc.options.getCameraType().isFirstPerson();

    double fovDeg = mc.options.fov().get();
    double halfV = Math.toRadians(fovDeg * 0.5);
    double halfDiag = Math.atan(Math.tan(halfV) * Math.sqrt(1.0 + ASSUMED_ASPECT * ASSUMED_ASPECT));
    double total = halfDiag + Math.toRadians(CinderConfig.INSTANCE.frustumMarginDegrees);
    cosFrustum = total >= Math.PI ? -1.0 : Math.cos(total);

    active = true;
  }

  public static boolean cacheHasRoom() {
    return OCCLUSION.size() < OCCLUSION_CACHE_LIMIT;
  }
}
