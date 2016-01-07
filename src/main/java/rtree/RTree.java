package rtree;

import com.google.common.base.Preconditions;

public class RTree<T> {

  private final int dimensions;

  public RTree(int dimensions) {
    this.dimensions = dimensions;
  }

  public int dimensions() {
    return dimensions;
  }

  public static <P> Builder<P> builder() {
    return new Builder<>();
  }

  public void insert(SpatialKey key, T data) {

  }

  public static class Builder<T> {

    private int dimensions = 2;

    public RTree<T> create() {
      return new RTree<>(dimensions);
    }

    public Builder<T> dimensions(int dimensions) {
      Preconditions.checkArgument(dimensions > 0, "Dimensions must be positive, you set %s", dimensions);
      this.dimensions = dimensions;
      return this;
    }
  }
}