package rtree;

import com.google.common.base.Preconditions;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RTree<T> {

  private final int dimensions;

  private final NodeSplitter splitter;

  private final NodeFactory nodeFactory;

  private TreeNode rootNode = null;

  public RTree(int dimensions, NodeSplitter splitter) {
    this.dimensions = dimensions;
    this.splitter = splitter;
    nodeFactory = NodeFactory.inMemory();
  }

  public int dimensions() {
    return dimensions;
  }

  public void insert(final SpatialKey key, final T data) {
    checkKeyDimensions(key);
    new InsertionPerformer(nodeFactory.leaf(key, data)).insert();
  }

  public Set<T> intersection(final SpatialKey queryKey) {
    return new TreeSearcher((node) -> queryKey.intersects(node.spatialKey())).search();
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

    private NodeSplitter splitter = new LongestBoundSplitter(4, 10);

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
        rootNode = nodeFactory.treeNode(leafNode.spatialKey());
        rootNode.addSubNode(leafNode);
      } else {
        Optional<Set<TreeNode>> split = insertToSubNode(rootNode);
        split.ifPresent(this::makeNewRoot);
      }
    }

    private Optional<Set<TreeNode>> insertToSubNode(TreeNode node) {
      if (subNodesAreLeaves(node)) {
        node.addSubNode(leafNode);
      } else {
        TreeNode subNode = chooseSubNode(node);
        Optional<Set<TreeNode>> split = insertToSubNode(subNode);
        split.ifPresent(nodes -> replaceWithNodes(node, subNode, nodes));
      }
      return splitIfNecessaryAndUpdateKey(node);
    }

    private void replaceWithNodes(TreeNode node, TreeNode subNode, Set<TreeNode> newNodes) {
      node.subNodes().remove(subNode);
      newNodes.forEach(node::addSubNode);
    }

    private Optional<Set<TreeNode>> splitIfNecessaryAndUpdateKey(TreeNode node) {
      Optional<Set<TreeNode>> split = splitter.split(node);
      if (split.isPresent()) {
        split.get().forEach(this::update);
      } else {
        update(node);
      }
      return split;
    }

    private void makeNewRoot(Set<TreeNode> nodes) {
      rootNode = nodeFactory.treeNode(leafNode.spatialKey());
      nodes.forEach(rootNode::addSubNode);
      update(rootNode);
    }

    private void update(TreeNode node) {
      SpatialKey unionKey = node.subNodes()
          .stream()
          .map(Node::spatialKey)
          .collect(Collectors.reducing(SpatialKey::union))
          .get();
      node.spatialKey(unionKey);
    }

    public TreeNode chooseSubNode(TreeNode node) {
      Node chosenSubNode = node.subNodes()
          .stream()
          .min(Comparator.comparingDouble(this::volumeIncrease))
          .orElseThrow(IllegalStateException::new);
      if (chosenSubNode instanceof TreeNode) {
        return (TreeNode) chosenSubNode;
      } else {
        throw new IllegalStateException("Incorrect node type");
      }
    }

    private double volumeIncrease(Node node) {
      double volume = node.spatialKey().volume();
      double volumeWithLeaf = node.spatialKey().union(leafNode.spatialKey()).volume();
      return volumeWithLeaf - volume;
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

    public Set<T> search() {
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