package rtree.factories;

import rtree.LeafNode;
import rtree.SpatialKey;
import rtree.TreeNode;

public interface NodeFactory {

  TreeNode treeNode(SpatialKey key);

  <T> LeafNode<T> leaf(SpatialKey key, T data);

  static NodeFactory inMemory() {
    return new InMemoryNodeFactory();
  }

  class InMemoryNodeFactory implements NodeFactory {
    @Override
    public TreeNode treeNode(SpatialKey key) {
      return new TreeNode.InMemory(key);
    }

    @Override
    public <T> LeafNode<T> leaf(SpatialKey key, T data) {
      return new LeafNode.InMemory<>(key, data);
    }
  }
}
