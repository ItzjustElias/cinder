package be.eliasb.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class CinderConfigScreen {
  private CinderConfigScreen() {}

  private static Option<Boolean> bool(
      String key,
      boolean def,
      java.util.function.Supplier<Boolean> getter,
      java.util.function.Consumer<Boolean> setter) {
    return Option.<Boolean>createBuilder()
        .name(Component.translatable("cinder.option." + key + ".name"))
        .description(
            dev.isxander.yacl3.api.OptionDescription.of(
                Component.translatable("cinder.option." + key + ".desc")))
        .binding(def, getter, setter)
        .controller(TickBoxControllerBuilder::create)
        .build();
  }

  private static Option<Integer> slider(
      String key,
      int def,
      int min,
      int max,
      int step,
      java.util.function.Supplier<Integer> getter,
      java.util.function.Consumer<Integer> setter) {
    return Option.<Integer>createBuilder()
        .name(Component.translatable("cinder.option." + key + ".name"))
        .description(
            dev.isxander.yacl3.api.OptionDescription.of(
                Component.translatable("cinder.option." + key + ".desc")))
        .binding(def, getter, setter)
        .controller(o -> IntegerSliderControllerBuilder.create(o).range(min, max).step(step))
        .build();
  }

  public static Screen createScreen(Screen parent) {
    var c = CinderConfig.INSTANCE;

    return YetAnotherConfigLib.createBuilder()
        .title(Component.translatable("cinder.config.title"))
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("cinder.category.culling"))
                .option(
                    bool(
                        "frustum",
                        true,
                        () -> c.enableFrustumCulling,
                        v -> c.enableFrustumCulling = v))
                .option(
                    slider(
                        "frustumMargin",
                        20,
                        0,
                        90,
                        1,
                        () -> c.frustumMarginDegrees,
                        v -> c.frustumMarginDegrees = v))
                .option(
                    bool(
                        "occlusion",
                        true,
                        () -> c.enableOcclusionCulling,
                        v -> c.enableOcclusionCulling = v))
                .option(
                    slider(
                        "occlusionMinDistance",
                        6,
                        2,
                        64,
                        1,
                        () -> c.occlusionMinDistance,
                        v -> c.occlusionMinDistance = v))
                .option(
                    slider(
                        "maxDistance",
                        32,
                        8,
                        256,
                        1,
                        () -> c.maxParticleDistance,
                        v -> c.maxParticleDistance = v))
                .option(
                    bool(
                        "relativeDistance",
                        false,
                        () -> c.useRelativeDistance,
                        v -> c.useRelativeDistance = v))
                .option(
                    slider(
                        "relativeDistancePercent",
                        100,
                        10,
                        400,
                        10,
                        () -> c.relativeDistancePercent,
                        v -> c.relativeDistancePercent = v))
                .option(
                    slider(
                        "maxPerTick",
                        1500,
                        50,
                        20000,
                        50,
                        () -> c.maxParticlesPerTick,
                        v -> c.maxParticlesPerTick = v))
                .build())
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("cinder.category.throttling_visuals"))
                .option(
                    bool(
                        "density",
                        true,
                        () -> c.enableDensityThrottling,
                        v -> c.enableDensityThrottling = v))
                .option(
                    slider(
                        "densityDrop",
                        75,
                        0,
                        100,
                        1,
                        () -> c.maxDensityDropPercent,
                        v -> c.maxDensityDropPercent = v))
                .option(bool("physics", true, () -> c.optimizePhysics, v -> c.optimizePhysics = v))
                .option(
                    slider(
                        "physicsDistance",
                        16,
                        4,
                        128,
                        1,
                        () -> c.physicsDisableDistance,
                        v -> c.physicsDisableDistance = v))
                .option(bool("fading", true, () -> c.smoothFading, v -> c.smoothFading = v))
                .build())
        .save(CinderConfig::save)
        .build()
        .generateScreen(parent);
  }
}
