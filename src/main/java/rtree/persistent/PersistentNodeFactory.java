package rtree.persistent;

import rtree.LeafNode;
import rtree.NodeFactory;
import rtree.SpatialKey;
import rtree.TreeNode;

public class PersistentNodeFactory implements NodeFactory {

  public PersistentNodeFactory(String filename) {

  }

  @Override
  public TreeNode treeNode(SpatialKey key) {
    return new PersistentTreeNode(key);
  }

  @Override
  public <T> LeafNode<T> leaf(SpatialKey key, T data) {
    return new PersistentLeafNode<T>(key, data);
  }
}
