package rtree.persistent;

import rtree.LeafNode;
import rtree.SpatialKey;

public class PersistentLeafNode<T> extends PersistentNode implements LeafNode<T> {

  public PersistentLeafNode(SpatialKey key, T data) {
  }

  @Override
  public T data() {
    return null;
  }
}
