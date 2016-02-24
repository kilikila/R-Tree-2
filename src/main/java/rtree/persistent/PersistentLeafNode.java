package rtree.persistent;

import rtree.LeafNode;
import rtree.SpatialKey;

public class PersistentLeafNode<T> extends PersistentNode implements LeafNode<T> {

  static final String HEADER_DATA = "data";

  public PersistentLeafNode(Page page, SpatialKey key, T data) {
    super(page, key);
    page.writeByHeader(HEADER_DATA, data);
  }

  @Override
  public T data() {
    return page.getByHeader(HEADER_DATA);
  }
}
