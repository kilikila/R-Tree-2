package rtree;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpatialKey {

  private final List<Bound> bounds;

  SpatialKey(final List<Bound> bounds) {
    this.bounds = bounds;
  }

  Bound bound(int dimension) {
    return bounds.get(dimension);
  }

  Bound bound(int dimension, double min, double max) {
    return bounds.set(dimension, new Bound(min, max));
  }

  public int dimensions() {
    return bounds.size();
  }

  public boolean intersects(SpatialKey other) {
    checkDimensions(other);
    for (int i = 0; i < dimensions(); i++)
      if (bound(i).intersects(other.bound(i))) return true;
    return false;
  }

  public SpatialKey union(SpatialKey other) {
    checkDimensions(other);
    List<Bound> bounds = IntStream.range(0, dimensions())
        .mapToObj(i -> bound(i).union(other.bound(i)))
        .collect(Collectors.toList());
    return new SpatialKey(bounds);
  }

  public double volume() {
    Optional<Double> volume = bounds.stream().map(Bound::length).collect(Collectors.reducing((l1, l2) -> l1 * l2));
    return volume.get();
  }

  public static class Bound {

    private final double min;

    private final double max;

    public Bound(double min, double max) {
      checkMinMax(min, max);
      this.min = min;
      this.max = max;
    }

    public double min() {
      return min;
    }

    public double max() {
      return max;
    }

    public boolean intersects(Bound other) {
      return (max >= other.min && max <= other.max)
          || (other.max >= min && other.max <= max);
    }

    public Bound union(Bound other) {
      return new Bound(Math.min(min, other.min), Math.max(max, other.max));
    }

    public double length() {
      return max - min;
    }
  }
  public static Builder create(int dimensions) {
    return new Builder(dimensions);
  }

  private static class Builder {

    private final SpatialKey key;

    public Builder(int dimensions) {
      List<Bound> bounds = IntStream.range(0, dimensions)
          .mapToObj(i -> new Bound(0, 0))
          .collect(Collectors.toList());
      key = new SpatialKey(bounds);
    }

    public void setBound(int dimension, double min, double max) {
      checkMinMax(min, max);
      key.bound(dimension, min, max);
    }

  }

  private static void checkMinMax(double min, double max) {
    Preconditions.checkArgument(min <= max, "Min must be less than or equal to max");
  }

  private void checkDimensions(SpatialKey other) {
    Preconditions.checkArgument(dimensions() == other.dimensions(),
        "Spatial keys have different number of dimensions");
  }
}
