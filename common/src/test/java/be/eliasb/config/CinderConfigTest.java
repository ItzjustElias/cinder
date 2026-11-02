package be.eliasb.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CinderConfigTest {
  @TempDir Path dir;

  private Path file;

  @BeforeEach
  void freshConfig() {
    CinderConfig.reset();
    file = dir.resolve("cinder.json");
  }

  @Test
  void defaultsAreSensible() {
    var c = CinderConfig.INSTANCE;
    assertEquals(32, c.maxParticleDistance);
    assertEquals(1500, c.maxParticlesPerTick);
    assertTrue(c.enableFrustumCulling);
    assertTrue(c.enableOcclusionCulling);
    assertTrue(c.particleTypeOverrides.isEmpty());
  }

  @Test
  void missingFileIsCreatedWithDefaults() throws IOException {
    CinderConfig.load(file);

    assertTrue(Files.exists(file));
    assertTrue(Files.readString(file).contains("maxParticleDistance"));
  }

  @Test
  void outOfRangeValuesAreClamped() throws IOException {
    Files.writeString(
        file,
        """
        {
          "maxParticleDistance": 9999,
          "maxParticlesPerTick": 1,
          "maxDensityDropPercent": -5,
          "frustumMarginDegrees": 500,
          "relativeDistancePercent": 0
        }
        """);

    CinderConfig.load(file);

    var c = CinderConfig.INSTANCE;
    assertEquals(256, c.maxParticleDistance);
    assertEquals(50, c.maxParticlesPerTick);
    assertEquals(0, c.maxDensityDropPercent);
    assertEquals(90, c.frustumMarginDegrees);
    assertEquals(10, c.relativeDistancePercent);
  }

  @Test
  void brokenJsonFallsBackToDefaults() throws IOException {
    Files.writeString(file, "{ this is not json");

    CinderConfig.load(file);

    assertEquals(32, CinderConfig.INSTANCE.maxParticleDistance);
  }

  @Test
  void fieldsMissingFromTheFileKeepTheirDefaults() throws IOException {
    Files.writeString(file, "{ \"maxParticleDistance\": 64 }");

    CinderConfig.load(file);

    assertEquals(64, CinderConfig.INSTANCE.maxParticleDistance);
    assertEquals(1500, CinderConfig.INSTANCE.maxParticlesPerTick);
  }

  @Test
  void particleTypeSettingsSurviveASaveAndLoad() {
    CinderConfig.INSTANCE.particleTypeOverrides.put("minecraft:portal", 0.3f);
    CinderConfig.INSTANCE.particleTypeNeverCull.add("minecraft:flame");
    CinderConfig.saveNow(file);

    CinderConfig.reset();
    CinderConfig.load(file);

    assertEquals(0.3f, CinderConfig.INSTANCE.getSpawnChance("minecraft:portal"));
    assertTrue(CinderConfig.INSTANCE.particleTypeNeverCull.contains("minecraft:flame"));
  }

  @Test
  void spawnChancesAreClampedToZeroAndOne() throws IOException {
    Files.writeString(
        file,
        """
        { "particleTypeOverrides": { "minecraft:a": 5.0, "minecraft:b": -2.0 } }
        """);

    CinderConfig.load(file);

    assertEquals(1.0f, CinderConfig.INSTANCE.getSpawnChance("minecraft:a"));
    assertEquals(0.0f, CinderConfig.INSTANCE.getSpawnChance("minecraft:b"));
  }

  @Test
  void aNaNSpawnChanceBecomesOne() {
    CinderConfig.INSTANCE.particleTypeOverrides.put("minecraft:a", Float.NaN);

    CinderConfig.saveNow(file);

    assertEquals(1.0f, CinderConfig.INSTANCE.getSpawnChance("minecraft:a"));
  }

  @Test
  void nullCollectionsInTheFileAreReplacedWithEmptyOnes() throws IOException {
    Files.writeString(file, "{ \"particleTypeOverrides\": null, \"particleTypeNeverCull\": null }");

    CinderConfig.load(file);

    assertNotNull(CinderConfig.INSTANCE.particleTypeOverrides);
    assertNotNull(CinderConfig.INSTANCE.particleTypeNeverCull);
  }

  @Test
  void unknownParticleTypesAreNotThrottled() {
    assertEquals(1.0f, CinderConfig.INSTANCE.getSpawnChance("minecraft:does_not_exist"));
  }
}
