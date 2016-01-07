package rtree;

import com.google.common.base.Preconditions;

import java.util.List;

public class HyperBox implements SpatialKey{

  private final List<Bound> bounds;

  public HyperBox(final List<Bound> bounds) {
    this.bounds = bounds;
  }

  public static class Bound {

    private final double min;

    private final double max;

    public Bound(double min, double max) {
      Preconditions.checkArgument(min <= max, "Min must be less than or equal to max");
      this.min = min;
      this.max = max;
    }
  }
}
