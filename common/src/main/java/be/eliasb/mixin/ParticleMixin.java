package be.eliasb.mixin;

import be.eliasb.CinderState;
import be.eliasb.config.CinderConfig;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Particle.class)
public abstract class ParticleMixin {
  @Unique private static final int CINDER$FADE_TICKS = 10;

  @Shadow protected double x;
  @Shadow protected double y;
  @Shadow protected double z;
  @Shadow protected boolean hasPhysics;
  @Shadow protected int age;
  @Shadow protected int lifetime;

  @Unique private float cinder$baseAlpha = -1.0F;
  @Unique private boolean cinder$physicsDisabled;
  @Unique private boolean cinder$lightCacheValid;
  @Unique private int cinder$cachedLightAge;
  @Unique private int cinder$cachedLightX;
  @Unique private int cinder$cachedLightY;
  @Unique private int cinder$cachedLightZ;
  @Unique private int cinder$cachedLightCoords;

  @Inject(method = "getLightCoords", at = @At("HEAD"), cancellable = true)
  private void cinder$useCachedLightCoords(float partialTick, CallbackInfoReturnable<Integer> cir) {
    if (!CinderConfig.INSTANCE.cacheLightPerTick) {
      cinder$lightCacheValid = false;
      return;
    }

    int blockX = Mth.floor(this.x);
    int blockY = Mth.floor(this.y);
    int blockZ = Mth.floor(this.z);
    if (cinder$lightCacheValid
        && cinder$cachedLightAge == this.age
        && cinder$cachedLightX == blockX
        && cinder$cachedLightY == blockY
        && cinder$cachedLightZ == blockZ) {
      cir.setReturnValue(cinder$cachedLightCoords);
    }
  }

  @Inject(method = "getLightCoords", at = @At("RETURN"))
  private void cinder$storeLightCoords(float partialTick, CallbackInfoReturnable<Integer> cir) {
    if (!CinderConfig.INSTANCE.cacheLightPerTick) {
      return;
    }

    cinder$cachedLightAge = this.age;
    cinder$cachedLightX = Mth.floor(this.x);
    cinder$cachedLightY = Mth.floor(this.y);
    cinder$cachedLightZ = Mth.floor(this.z);
    cinder$cachedLightCoords = cir.getReturnValue();
    cinder$lightCacheValid = true;
  }

  @Inject(method = "tick", at = @At("HEAD"))
  private void cinder$optimizeAndFade(CallbackInfo ci) {
    var cfg = CinderConfig.INSTANCE;

    if (cfg.smoothFading && (Object) this instanceof SingleQuadParticleAccessor quad) {
      int remaining = this.lifetime - this.age;
      if (remaining > 0 && remaining <= CINDER$FADE_TICKS) {
        if (this.cinder$baseAlpha < 0.0F) {
          this.cinder$baseAlpha = quad.cinder$getAlpha();
        }
        quad.cinder$setAlpha(this.cinder$baseAlpha * ((float) remaining / CINDER$FADE_TICKS));
      }
    }

    if (this.cinder$physicsDisabled) {
      this.hasPhysics = true;
      this.cinder$physicsDisabled = false;
    }

    if (cfg.optimizePhysics && this.hasPhysics && CinderState.active) {
      double dx = this.x - CinderState.camX;
      double dy = this.y - CinderState.camY;
      double dz = this.z - CinderState.camZ;
      double limit = cfg.physicsDisableDistance;
      if (dx * dx + dy * dy + dz * dz > limit * limit) {
        this.hasPhysics = false;
        this.cinder$physicsDisabled = true;
      }
    }
  }
}
