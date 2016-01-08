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

  @Override
  public boolean intersects(SpatialKey spatialKey) {
    return false;
  }

  @Override
  public SpatialKey union(SpatialKey key) {
    return null;
  }

  public BoxBound bound(int dim) {
    return bounds.get(dim);
  }

}
