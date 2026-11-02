package be.eliasb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CinderMathTest {
  private static final double EPS = 1e-4;

  @Test
  void frustumCosineMatchesHandCalculatedValues() {
    assertEquals(0.3665, CinderMath.frustumCosine(90, 0), EPS);
    assertEquals(0.1627, CinderMath.frustumCosine(70, 20), EPS);
  }

  @Test
  void biggerMarginMeansWiderCone() {
    double narrow = CinderMath.frustumCosine(70, 0);
    double wide = CinderMath.frustumCosine(70, 40);
    assertTrue(wide < narrow);
  }

  @Test
  void coneThatCoversEverythingReturnsMinusOne() {
    assertEquals(-1.0, CinderMath.frustumCosine(90, 120));
  }

  @Test
  void particleStraightAheadIsInsideAndBehindIsOutside() {
    double limit = CinderMath.frustumCosine(70, 20);
    assertFalse(CinderMath.outsideCone(0, 0, 10, 10, 0, 0, 1, limit));
    assertTrue(CinderMath.outsideCone(0, 0, -10, 10, 0, 0, 1, limit));
  }

  @Test
  void coneEdgeIsAroundEightyDegreesForDefaultSettings() {
    double limit = CinderMath.frustumCosine(70, 20);
    double dist = 10;
    double in = Math.toRadians(70);
    double out = Math.toRadians(85);
    assertFalse(
        CinderMath.outsideCone(Math.sin(in) * dist, 0, Math.cos(in) * dist, dist, 0, 0, 1, limit));
    assertTrue(
        CinderMath.outsideCone(
            Math.sin(out) * dist, 0, Math.cos(out) * dist, dist, 0, 0, 1, limit));
  }

  @Test
  void densityDropIsZeroUpToTheStartDistance() {
    assertEquals(0.0, CinderMath.densityDropChance(3, 32, 8, 75));
    assertEquals(0.0, CinderMath.densityDropChance(8, 32, 8, 75));
  }

  @Test
  void densityDropRisesLinearlyToTheConfiguredMaximum() {
    assertEquals(0.375, CinderMath.densityDropChance(20, 32, 8, 75), EPS);
    assertEquals(0.75, CinderMath.densityDropChance(32, 32, 8, 75), EPS);
  }

  @Test
  void densityDropIsZeroWhenTheRangeIsNotWiderThanTheStart() {
    assertEquals(0.0, CinderMath.densityDropChance(10, 8, 8, 75));
  }

  @Test
  void relativeDistanceFollowsRenderDistance() {
    assertEquals(192.0, CinderMath.effectiveMaxDistance(true, 12, 100, 32));
    assertEquals(96.0, CinderMath.effectiveMaxDistance(true, 12, 50, 32));
  }

  @Test
  void fixedDistanceIgnoresRenderDistance() {
    assertEquals(32.0, CinderMath.effectiveMaxDistance(false, 12, 100, 32));
  }
}
