package rtree;

import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpatialKey implements Serializable{

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
    checkDimensionsMatch(other);
    for (int i = 0; i < dimensions(); i++)
      if (bound(i).intersects(other.bound(i))) return true;
    return false;
  }

  public SpatialKey union(SpatialKey other) {
    checkDimensionsMatch(other);
    List<Bound> bounds = IntStream.range(0, dimensions())
        .mapToObj(i -> bound(i).union(other.bound(i)))
        .collect(Collectors.toList());
    return new SpatialKey(bounds);
  }

  public double volume() {
    Optional<Double> volume = bounds.stream().map(Bound::length).collect(Collectors.reducing((l1, l2) -> l1 * l2));
    return volume.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SpatialKey that = (SpatialKey) o;
    return IntStream.range(0, dimensions())
        .mapToObj(i -> this.bound(i).equals(that.bound(i)))
        .allMatch(b -> b.equals(true));

  }

  @Override
  public int hashCode() {
    return bounds.hashCode();
  }

  public static class Bound implements Serializable{

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

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Bound bound = (Bound) o;
      return Double.compare(bound.min, min) == 0 && Double.compare(bound.max, max) == 0;

    }

    @Override
    public int hashCode() {
      int result;
      long temp;
      temp = Double.doubleToLongBits(min);
      result = (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(max);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      return result;
    }
  }

  public static Builder builder(int dimensions) {
    return new Builder(dimensions);
  }

  public static class Builder {

    private final SpatialKey key;

    public Builder(int dimensions) {
      List<Bound> bounds = IntStream.range(0, dimensions)
          .mapToObj(i -> new Bound(0, 0))
          .collect(Collectors.toList());
      key = new SpatialKey(bounds);
    }

    public Builder setBound(int dimension, double min, double max) {
      checkDimensionSupported(dimension);
      checkMinMax(min, max);
      key.bound(dimension, min, max);
      return this;
    }

    private void checkDimensionSupported(int dimension) {
      Preconditions.checkArgument(dimension < key.dimensions(), "Incorrect dimension: %s", dimension);
    }

    public SpatialKey create() {
      return key;
    }

  }

  private static void checkMinMax(double min, double max) {
    Preconditions.checkArgument(min <= max, "Min must be less than or equal to max");
  }

  private void checkDimensionsMatch(SpatialKey other) {
    Preconditions.checkArgument(dimensions() == other.dimensions(),
        "Spatial keys have different number of dimensions");
  }
}
