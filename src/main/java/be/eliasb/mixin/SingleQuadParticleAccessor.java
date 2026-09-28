package be.eliasb.mixin;

import net.minecraft.client.particle.SingleQuadParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SingleQuadParticle.class)
public interface SingleQuadParticleAccessor {
    @Accessor("alpha")
    float cinder$getAlpha();

    @Accessor("alpha")
    void cinder$setAlpha(float alpha);
}
