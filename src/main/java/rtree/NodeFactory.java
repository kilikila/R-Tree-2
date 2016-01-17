package rtree;

public interface NodeFactory {

  default TreeNode treeNode(SpatialKey key) {
    return new TreeNode(key);
  }

  default <T> LeafNode<T> leaf(SpatialKey key, T data) {
    return new LeafNode<>(key, data);
  }

  static NodeFactory inMemory() {
    return new NodeFactory() {};
  }
}
