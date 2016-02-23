package rtree.persistent;

import rtree.Node;
import rtree.SpatialKey;

public abstract class PersistentNode implements Node {

  protected final Page page;

  protected PersistentNode(Page page, SpatialKey key) {
    this.page = page;
    spatialKey(key);
  }

  @Override
  public SpatialKey spatialKey() {
    return page.getByHeader("key");
  }

  @Override
  public void spatialKey(SpatialKey key) {
    page.writeByHeader("key", key);
  }
}
