package rtree.persistent;

import rtree.LeafNode;
import rtree.SpatialKey;

public class PersistentLeafNode<T> extends PersistentNode implements LeafNode<T> {

  public PersistentLeafNode(Page page, SpatialKey key, T data) {
    super(page, key);
    page.writeByHeader("data", data);
  }

  @Override
  public T data() {
    return page.getByHeader("data");
  }
}
