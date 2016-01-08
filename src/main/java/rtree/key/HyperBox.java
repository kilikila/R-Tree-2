package rtree.key;

import rtree.SpatialKey;

import java.util.List;

public class HyperBox implements SpatialKey {

  private final List<BoxBound> bounds;

  public HyperBox(final List<BoxBound> bounds) {
    this.bounds = bounds;
  }

  @Override
  public int dimensions() {
    return bounds.size();
  }

  public BoxBound bound(int dim) {
    return bounds.get(dim);
  }

}
