package be.eliasb;

import be.eliasb.config.CinderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ThreadLocalRandom;

public final class CinderCulling {
    private static final double FRUSTUM_MIN_DISTANCE = 3.0;
    private static final double DENSITY_START = 8.0;

    private CinderCulling() {}

    public static boolean shouldCull(ParticleOptions options, double x, double y, double z) {
        var cfg = CinderConfig.INSTANCE;
        if (!cfg.particleTypeOverrides.isEmpty()) {
            float chance = cfg.getSpawnChance(particleTypeId(options));
            if (chance <= 0.0f) {
                return true;
            }
            if (chance < 1.0f && ThreadLocalRandom.current().nextFloat() >= chance) {
                return true;
            }
        }

        if (!CinderState.active) {
            return false;
        }

        if (CinderState.spawnedThisTick >= cfg.maxParticlesPerTick) {
            return true;
        }

        double dx = x - CinderState.camX;
        double dy = y - CinderState.camY;
        double dz = z - CinderState.camZ;
        double distSq = dx * dx + dy * dy + dz * dz;
        double maxDist = CinderState.effectiveMaxDistance;

        if (distSq > maxDist * maxDist) {
            return true;
        }
        double dist = Math.sqrt(distSq);

        if (cfg.enableFrustumCulling && CinderState.frustumUsable && dist > FRUSTUM_MIN_DISTANCE) {
            double dot = (dx * CinderState.lookX + dy * CinderState.lookY + dz * CinderState.lookZ) / dist;
            if (dot < CinderState.cosFrustum) {
                return true;
            }
        }

        if (cfg.enableDensityThrottling && dist > DENSITY_START && maxDist > DENSITY_START) {
            double factor = (dist - DENSITY_START) / (maxDist - DENSITY_START);
            double dropChance = factor * (cfg.maxDensityDropPercent / 100.0);
            if (ThreadLocalRandom.current().nextDouble() < dropChance) {
                return true;
            }
        }

        if (cfg.enableOcclusionCulling && dist > cfg.occlusionMinDistance && isOccluded(x, y, z)) {
            return true;
        }

        CinderState.spawnedThisTick++;
        return false;
    }

    private static String particleTypeId(ParticleOptions options) {
        var key = BuiltInRegistries.PARTICLE_TYPE.getKey(options.getType());
        return key == null ? "unknown:unknown" : key.toString();
    }

    private static boolean isOccluded(double x, double y, double z) {
        int bx = Mth.floor(x);
        int by = Mth.floor(y);
        int bz = Mth.floor(z);
        long key = BlockPos.asLong(bx, by, bz);

        byte cached = CinderState.OCCLUSION.get(key);
        if (cached >= 0) {
            return cached == 1;
        }

        boolean occluded = raycast(x, y, z, bx, by, bz);
        if (CinderState.cacheHasRoom()) {
            CinderState.OCCLUSION.put(key, (byte) (occluded ? 1 : 0));
        }
        return occluded;
    }

    private static boolean raycast(double x, double y, double z, int bx, int by, int bz) {
        var level = Minecraft.getInstance().level;
        if (level == null || CinderState.eye == null) {
            return false;
        }

        var context = new ClipContext(
                CinderState.eye,
                new Vec3(x, y, z),
                ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE,
                CinderState.viewer);

        BlockHitResult hit = level.clip(context);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return false;
        }

        BlockPos hitPos = hit.getBlockPos();
        if (hitPos.getX() == bx && hitPos.getY() == by && hitPos.getZ() == bz) {
            return false;
        }
        return level.getBlockState(hitPos).canOcclude();
    }
}
