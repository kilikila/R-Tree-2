package rtree.persistent;

import rtree.Node;
import rtree.SpatialKey;

public abstract class PersistentNode implements Node {

  static final String HEADER_KEY = "key";

  protected final PageAccessor pageAccessor;

  protected final PageId id;

  protected PersistentNode(PageId id, SpatialKey key, PageAccessor pageAccessor) {
    this.id = id;
    this.pageAccessor = pageAccessor;
    spatialKey(key);
  }

  @Override
  public SpatialKey spatialKey() {
    return page().getByHeader(HEADER_KEY);
  }

  protected Page page() {
    return pageAccessor.getById(id);
  }

  @Override
  public void spatialKey(SpatialKey key) {
    page().writeByHeader(HEADER_KEY, key);
  }
}
