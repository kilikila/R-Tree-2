package rtree.key;

import com.google.common.base.Preconditions;

public class BoxBound {

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
