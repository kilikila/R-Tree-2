package rtree.factories;

import rtree.LeafNode;
import rtree.SpatialKey;
import rtree.TreeNode;

public interface NodeFactory {

  default TreeNode treeNode(SpatialKey key) {
    return new TreeNode.InMemory(key);
  }

  default <T> LeafNode<T> leaf(SpatialKey key, T data) {
    return new LeafNode.InMemory<>(key, data);
  }

  static NodeFactory inMemory() {
    return new NodeFactory() {};
  }
}
