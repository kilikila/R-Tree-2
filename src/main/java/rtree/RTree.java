package rtree;

import com.google.common.base.Preconditions;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class RTree<T> {

  private final int dimensions;

  private NodeSplitter splitter;

  private TreeNode rootNode = null;

  public RTree(int dimensions, NodeSplitter splitter) {
    this.dimensions = dimensions;
    this.splitter = splitter;
  }

  public int dimensions() {
    return dimensions;
  }

  public void insert(final SpatialKey key, final T data) {
    checkKeyDimensions(key);
    new InsertionPerformer(new LeafNode<>(key, data)).insert();
  }

  public Set<T> intersection(final SpatialKey queryKey) {
    return new TreeSearcher((node) -> queryKey.intersects(node.spatialKey())).find();
  }

  public void clear() {
    rootNode = null;
  }

  public boolean isEmpty() {
    return rootNode == null;
  }

  private void checkKeyDimensions(final SpatialKey key) {
    Preconditions.checkArgument(key.dimensions() == dimensions,
        "Attempt to insert by key with unsupported number dimensions. Expected: %s, got: %s",
        dimensions, key.dimensions());
  }

  public static Builder<Object> builder() {
    return new Builder<>();
  }

  public static class Builder<T> {

    private int dimensions = 2;

    private NodeSplitter splitter = new LinearSplitter(4, 10);

    public Builder<T> dimensions(int dimensions) {
      Preconditions.checkArgument(dimensions > 0, "Dimensions must be positive, you set %s", dimensions);
      this.dimensions = dimensions;
      return this;
    }

    public <D> Builder<D> dataType(Class<D> dataClass) {
      Builder<D> builder = new Builder<>();
      return builder.dimensions(dimensions).nodeSplitter(splitter);
    }

    public Builder<T> nodeSplitter(NodeSplitter splitter) {
      this.splitter = splitter;
      return this;
    }

    public RTree<T> create() {
      return new RTree<>(dimensions, splitter);
    }

  }

  private class InsertionPerformer {

    private final LeafNode<T> leafNode;

    public InsertionPerformer(LeafNode<T> leafNode) {
      this.leafNode = leafNode;
    }

    private void insert() {
      if (isEmpty()) {
        rootNode = new TreeNode(leafNode.spatialKey());
        rootNode.addSubNode(leafNode);
      } else {
        insertToSubNode(rootNode);
      }
    }

    private void insertToSubNode(TreeNode node) {
      if (!subNodesAreLeaves(node)) {
        TreeNode subNode = chooseSubNode(node);
        insertToSubNode(subNode);
      } else {
        node.addSubNode(leafNode);
      }
      updateSpatialKey(node);
    }

    private void updateSpatialKey(TreeNode node) {
      SpatialKey unionKey = node.subNodes()
          .stream()
          .map(Node::spatialKey)
          .collect(leafNode::spatialKey, SpatialKey::union, SpatialKey::union);
      node.spatialKey(unionKey);
    }

    private TreeNode chooseSubNode(TreeNode node) {
      return null;
    }

    private boolean subNodesAreLeaves(TreeNode node) {
      return node.subNodes().iterator().next() instanceof LeafNode;
    }

  }

  private class TreeSearcher {

    private final Predicate<Node> condition;

    private final Set<T> result = new HashSet<>();

    public TreeSearcher(Predicate<Node> condition) {
      this.condition = condition;
    }

    public Set<T> find() {
      if (!isEmpty()) {
        searchSubNodes(rootNode);
      }
      return result;
    }

    private void searchSubNodes(Node node) {
      if (node instanceof LeafNode) {
        result.add(((LeafNode<T>) node).data());
      } else {
        ((TreeNode) node).subNodes()
            .stream()
            .filter(condition)
            .forEach(this::searchSubNodes);
      }
    }

  }
}