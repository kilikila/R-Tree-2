package rtree.persistent;

import rtree.Node;
import rtree.SpatialKey;

public abstract class PersistentNode implements Node {

  @Override
  public SpatialKey spatialKey() {
    return null;
  }

  @Override
  public void spatialKey(SpatialKey key) {

  }
}
