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
      String name,
      String desc,
      boolean def,
      java.util.function.Supplier<Boolean> getter,
      java.util.function.Consumer<Boolean> setter) {
    return Option.<Boolean>createBuilder()
        .name(Component.literal(name))
        .description(dev.isxander.yacl3.api.OptionDescription.of(Component.literal(desc)))
        .binding(def, getter, setter)
        .controller(TickBoxControllerBuilder::create)
        .build();
  }

  private static Option<Integer> slider(
      String name,
      String desc,
      int def,
      int min,
      int max,
      int step,
      java.util.function.Supplier<Integer> getter,
      java.util.function.Consumer<Integer> setter) {
    return Option.<Integer>createBuilder()
        .name(Component.literal(name))
        .description(dev.isxander.yacl3.api.OptionDescription.of(Component.literal(desc)))
        .binding(def, getter, setter)
        .controller(o -> IntegerSliderControllerBuilder.create(o).range(min, max).step(step))
        .build();
  }

  public static Screen createScreen(Screen parent) {
    var c = CinderConfig.INSTANCE;

    return YetAnotherConfigLib.createBuilder()
        .title(Component.literal("Cinder"))
        .category(
            ConfigCategory.createBuilder()
                .name(Component.literal("Culling"))
                .option(
                    bool(
                        "Frustum (view cone) culling",
                        "Skip spawning particles outside your view.",
                        true,
                        () -> c.enableFrustumCulling,
                        v -> c.enableFrustumCulling = v))
                .option(
                    slider(
                        "Frustum margin (degrees)",
                        "Extra angle around the view cone.",
                        20,
                        0,
                        90,
                        1,
                        () -> c.frustumMarginDegrees,
                        v -> c.frustumMarginDegrees = v))
                .option(
                    bool(
                        "Occlusion culling",
                        "Skip particles hidden behind solid blocks.",
                        true,
                        () -> c.enableOcclusionCulling,
                        v -> c.enableOcclusionCulling = v))
                .option(
                    slider(
                        "Occlusion min distance",
                        "Never occlusion-test closer than this (blocks).",
                        6,
                        2,
                        64,
                        1,
                        () -> c.occlusionMinDistance,
                        v -> c.occlusionMinDistance = v))
                .option(
                    slider(
                        "Max particle distance",
                        "Hard cutoff in blocks. Ignored if relative distance is on.",
                        32,
                        8,
                        256,
                        1,
                        () -> c.maxParticleDistance,
                        v -> c.maxParticleDistance = v))
                .option(
                    bool(
                        "Scale distance with render distance",
                        "Use a percentage of your video-settings render distance instead of a fixed block count.",
                        false,
                        () -> c.useRelativeDistance,
                        v -> c.useRelativeDistance = v))
                .option(
                    slider(
                        "Relative distance (%)",
                        "Percent of render distance to use when the option above is on. 100 = exactly your render distance edge.",
                        100,
                        10,
                        400,
                        10,
                        () -> c.relativeDistancePercent,
                        v -> c.relativeDistancePercent = v))
                .option(
                    slider(
                        "Max particles per tick",
                        "Global spawn cap per tick.",
                        1500,
                        50,
                        20000,
                        50,
                        () -> c.maxParticlesPerTick,
                        v -> c.maxParticlesPerTick = v))
                .build())
        .category(
            ConfigCategory.createBuilder()
                .name(Component.literal("Throttling & Visuals"))
                .option(
                    bool(
                        "Density throttling",
                        "Randomly drop more distant particles.",
                        true,
                        () -> c.enableDensityThrottling,
                        v -> c.enableDensityThrottling = v))
                .option(
                    slider(
                        "Max density drop (%)",
                        "Drop chance at maximum distance.",
                        75,
                        0,
                        100,
                        1,
                        () -> c.maxDensityDropPercent,
                        v -> c.maxDensityDropPercent = v))
                .option(
                    bool(
                        "Optimize physics",
                        "Far particles skip block collision.",
                        true,
                        () -> c.optimizePhysics,
                        v -> c.optimizePhysics = v))
                .option(
                    slider(
                        "Physics disable distance",
                        "Blocks from camera.",
                        16,
                        4,
                        128,
                        1,
                        () -> c.physicsDisableDistance,
                        v -> c.physicsDisableDistance = v))
                .option(
                    bool(
                        "Smooth fading",
                        "Fade particles out over their last 10 ticks.",
                        true,
                        () -> c.smoothFading,
                        v -> c.smoothFading = v))
                .build())
        .save(CinderConfig::save)
        .build()
        .generateScreen(parent);
  }
}
