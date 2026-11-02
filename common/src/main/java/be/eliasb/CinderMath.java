package be.eliasb;

public final class CinderMath {
  public static final double ASSUMED_ASPECT = 21.0 / 9.0;

  private CinderMath() {}

  public static double frustumCosine(double fovDegrees, double marginDegrees) {
    double halfV = Math.toRadians(fovDegrees * 0.5);
    double halfDiag = Math.atan(Math.tan(halfV) * Math.sqrt(1.0 + ASSUMED_ASPECT * ASSUMED_ASPECT));
    double total = halfDiag + Math.toRadians(marginDegrees);
    return total >= Math.PI ? -1.0 : Math.cos(total);
  }

  public static boolean outsideCone(
      double dx,
      double dy,
      double dz,
      double dist,
      double lx,
      double ly,
      double lz,
      double cosLimit) {
    double dot = (dx * lx + dy * ly + dz * lz) / dist;
    return dot < cosLimit;
  }

  public static double densityDropChance(
      double dist, double maxDist, double start, int maxDropPercent) {
    if (dist <= start || maxDist <= start) {
      return 0.0;
    }
    double factor = (dist - start) / (maxDist - start);
    return factor * (maxDropPercent / 100.0);
  }

  public static double effectiveMaxDistance(
      boolean relative, int renderDistanceChunks, int relativePercent, int fixedDistance) {
    if (relative) {
      return renderDistanceChunks * 16.0 * (relativePercent / 100.0);
    }
    return fixedDistance;
  }
}
