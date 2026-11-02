package be.eliasb;

import be.eliasb.config.CinderConfig;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class CinderState {
  private static final int OCCLUSION_CACHE_LIMIT = 8192;

  public static boolean active;
  public static boolean frustumUsable;

  public static double camX, camY, camZ;
  public static double lookX, lookY, lookZ;
  public static double cosFrustum = -1.0;

  public static Vec3 eye;
  public static Entity viewer;

  public static int spawnedThisTick;
  public static int occlusionRaycastsThisTick;

  public static double effectiveMaxDistance = 32.0;
  public static final Long2ByteOpenHashMap OCCLUSION = new Long2ByteOpenHashMap();

  static {
    OCCLUSION.defaultReturnValue((byte) -1);
  }

  private CinderState() {}

  public static void beginTick(Minecraft mc) {
    spawnedThisTick = 0;
    occlusionRaycastsThisTick = 0;

    if (!OCCLUSION.isEmpty()) {
      OCCLUSION.clear();
    }

    var cfg = CinderConfig.INSTANCE;
    effectiveMaxDistance =
        CinderMath.effectiveMaxDistance(
            cfg.useRelativeDistance,
            mc.options.renderDistance().get(),
            cfg.relativeDistancePercent,
            cfg.maxParticleDistance);

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

    frustumUsable = mc.options.getCameraType().isFirstPerson();

    cosFrustum = CinderMath.frustumCosine(mc.options.fov().get(), cfg.frustumMarginDegrees);

    active = true;
  }

  public static boolean cacheHasRoom() {
    return OCCLUSION.size() < OCCLUSION_CACHE_LIMIT;
  }
}
