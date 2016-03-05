package rtree;

import com.google.common.base.Preconditions;
import rtree.factories.DivisionPerformerFactory;
import rtree.factories.NodeComparatorFactory;
import rtree.factories.NodeFactory;
import rtree.implementations.UniformDivisionPerformer;
import rtree.implementations.VolumeIncreaseNodeComparator;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RTree<T> {

  private final int dimensions;

  private final int minSubNodes;

  private final int maxSubNodes;

  private final NodeFactory nodeFactory;

  private final DivisionPerformerFactory divisionPerformerFactory;

  private final NodeComparatorFactory nodeComparatorFactory;

  private TreeNode rootNode = null;

  public RTree(int dimensions,
               int minSubNodes,
               int maxSubNodes,
               NodeFactory nodeFactory,
               DivisionPerformerFactory divisionPerformerFactory,
               NodeComparatorFactory nodeComparatorFactory) {
    this.dimensions = dimensions;
    this.minSubNodes = minSubNodes;
    this.maxSubNodes = maxSubNodes;
    this.nodeFactory = nodeFactory;
    this.divisionPerformerFactory = divisionPerformerFactory;
    this.nodeComparatorFactory = nodeComparatorFactory;
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
        Set<TreeNode> subNodes = castSubsToTreeNodes(node);
        TreeNode subNode = chooseNode(subNodes, leafNode);
        Optional<Set<TreeNode>> split = insertToSubNode(subNode);
        split.ifPresent(nodes -> replaceWithNodes(node, subNode, nodes));
      }
      return splitIfNecessaryAndUpdateKey(node);
    }

    private Set<TreeNode> castSubsToTreeNodes(TreeNode node) {
      return node.subNodes()
                .map(subNode -> (TreeNode) subNode)
                .collect(Collectors.toSet());
    }

    private void replaceWithNodes(TreeNode node, TreeNode subNode, Set<TreeNode> newNodes) {
      node.removeSub(subNode);
      newNodes.forEach(node::addSubNode);
    }

    private Optional<Set<TreeNode>> splitIfNecessaryAndUpdateKey(TreeNode node) {
      Optional<Set<TreeNode>> split = new NodeSplitter().split(node)
          .map(keys -> newNodes(keys, node));
      if (split.isPresent()) {
        split.get().forEach(this::update);
      } else {
        update(node);
      }
      return split;
    }

    private Set<TreeNode> newNodes(Set<SpatialKey> keys, TreeNode node) {
      Set<TreeNode> newNodes = keys.stream()
          .map(nodeFactory::treeNode)
          .collect(Collectors.toSet());
      node.subNodes().forEach(subNode -> chooseAndAdd(newNodes, subNode));
      return nonEmpty(newNodes);
    }

    private Set<TreeNode> nonEmpty(Set<TreeNode> newNodes) {
      return newNodes.stream().filter(newNode -> newNode.numOfSubs() > 0).collect(Collectors.toSet());
    }

    private void chooseAndAdd(Set<TreeNode> nodes, Node subNode) {
      chooseNode(nodes, subNode).addSubNode(subNode);
    }

    private TreeNode chooseNode(Set<TreeNode> nodes, Node nodeToInsert) {
      Comparator<Node> nodeComparator = nodeComparatorFactory.supplyComparator(nodeToInsert);
      Node chosenSubNode = nodes.stream()
          .min(nodeComparator)
          .orElseThrow(() -> new IllegalStateException("Cannot choose sub node"));
      return (TreeNode) chosenSubNode;
    }

    private void makeNewRoot(Set<TreeNode> nodes) {
      rootNode = nodeFactory.treeNode(leafNode.spatialKey());
      nodes.forEach(rootNode::addSubNode);
      update(rootNode);
    }

    private void update(TreeNode node) {
      SpatialKey unionKey = node.subNodes()
          .map(Node::spatialKey)
          .collect(Collectors.reducing(SpatialKey::union))
          .get();
      node.spatialKey(unionKey);
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

    @SuppressWarnings(value = {"unchecked"})
    private void searchSubNodes(Node node) {
      if (node instanceof LeafNode.InMemory) {
        result.add(((LeafNode<T>) node).data());
      } else {
        ((TreeNode) node).subNodes()
            .filter(condition)
            .forEach(this::searchSubNodes);
      }
    }

  }

  private class NodeSplitter {

    public Optional<Set<SpatialKey>> split(TreeNode node) {
      return Optional.ofNullable(isDividable(node) ? divide(subNodeKeys(node)) : null);
    }

    private Set<SpatialKey> subNodeKeys(TreeNode node) {
      return node.subNodes().map(Node::spatialKey).collect(Collectors.toSet());
    }

    protected Set<SpatialKey> divide(Set<SpatialKey> subNodeKeys) {
      return divisionPerformerFactory.create(subNodeKeys).divide(minSubNodes);
    }

    private boolean isDividable(TreeNode node) {
      return node.numOfSubs() > maxSubNodes;
    }

  }
  public static Builder<Object> builder() {
    return new Builder<>();
  }

  public static class Builder<T> {

    protected int dimensions = 2;

    protected int minSubNodes = 4;

    protected int maxSubNodes = 10;

    protected NodeFactory nodeFactory = NodeFactory.inMemory();

    protected DivisionPerformerFactory divisionPerformerFactory = UniformDivisionPerformer::new;

    protected NodeComparatorFactory nodeComparatorFactory = VolumeIncreaseNodeComparator::new;

    public Builder<T> dimensions(int dimensions) {
      Preconditions.checkArgument(dimensions > 0, "Dimensions must be positive, you set %s", dimensions);
      this.dimensions = dimensions;
      return this;
    }

    public Builder<T> setMinMax(int minSubNodes, int maxSubNodes) {
      checkMinMax(minSubNodes, maxSubNodes);
      this.minSubNodes = minSubNodes;
      this.maxSubNodes = maxSubNodes;
      return this;
    }

    public Builder<T> divisionPerformerFactory(DivisionPerformerFactory divisionPerformerFactory) {
      this.divisionPerformerFactory = divisionPerformerFactory;
      return this;
    }

    public Builder<T> nodeComparatorFactory(NodeComparatorFactory nodeComparatorFactory) {
      this.nodeComparatorFactory = nodeComparatorFactory;
      return this;
    }

    public <D> Builder<D> dataType(Class<D> dataClass) {
      return (Builder<D>) this;
    }

    public RTree<T> create() {
      return new RTree<>(dimensions, minSubNodes, maxSubNodes, nodeFactory, divisionPerformerFactory, nodeComparatorFactory);
    }

    private void checkMinMax(int minSubNodes, int maxSubNodes) {
      Preconditions.checkArgument(minSubNodes > 0, "Minimal num of nodes must be positive. Got %s", minSubNodes);
      Preconditions.checkArgument(maxSubNodes > minSubNodes, "Max must be grater then min");
    }
  }
}