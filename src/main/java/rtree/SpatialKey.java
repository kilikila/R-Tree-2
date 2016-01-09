package rtree;

import com.google.common.base.Preconditions;

import java.util.List;

public class SpatialKey {

  private final List<BoxBound> bounds;

  public SpatialKey(final List<BoxBound> bounds) {
    this.bounds = bounds;
  }

  public int dimensions() {
    return bounds.size();
  }

  public boolean intersects(SpatialKey spatialKey) {
    return false;
  }

  public SpatialKey union(SpatialKey key) {
    return null;
  }

  public BoxBound bound(int dim) {
    return bounds.get(dim);
  }

  public static class BoxBound {

    private final double min;

    private final double max;

    public BoxBound(double min, double max) {
      Preconditions.checkArgument(min <= max, "Min must be less than or equal to max");
      this.min = min;
      this.max = max;
    }

    public double min() {
      return min;
    }

    public double max() {
      return max;
    }

    public boolean intersects(BoxBound other) {
      return (max >= other.min && max <= other.max) || (other.max >= min && other.max <= max);
    }
  }
}
