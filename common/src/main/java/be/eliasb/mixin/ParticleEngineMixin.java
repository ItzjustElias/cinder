package be.eliasb.mixin;

import be.eliasb.CinderCulling;
import be.eliasb.CinderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {

  @Inject(method = "tick", at = @At("HEAD"))
  private void cinder$beginTick(CallbackInfo ci) {
    CinderState.beginTick(Minecraft.getInstance());
  }

  @Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
  private void cinder$cullSpawn(
      ParticleOptions options,
      double x,
      double y,
      double z,
      double xa,
      double ya,
      double za,
      CallbackInfoReturnable<Particle> cir) {
    if (CinderCulling.shouldCull(options, x, y, z)) {
      cir.setReturnValue(null);
    }
  }
}
