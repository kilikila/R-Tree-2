package rtree.persistent;

import rtree.Node;
import rtree.SpatialKey;

public abstract class PersistentNode implements Node {

  static final String HEADER_KEY = "key";

  protected final Page page;

  protected PersistentNode(Page page, SpatialKey key) {
    this.page = page;
    spatialKey(key);
  }

  @Override
  public SpatialKey spatialKey() {
    return page.getByHeader(HEADER_KEY);
  }

  @Override
  public void spatialKey(SpatialKey key) {
    page.writeByHeader(HEADER_KEY, key);
  }
}
