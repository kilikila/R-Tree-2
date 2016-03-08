package rtree;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import rtree.factories.DividerFactory;
import rtree.factories.KeyComparatorFactory;
import rtree.factories.NodeFactory;
import rtree.implementations.UniformDivider;
import rtree.implementations.VolumeIncreaseKeyComparator;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RTree<T> {

  protected final int dimensions;

  protected final int minSubNodes;

  protected final int maxSubNodes;

  protected final NodeFactory nodeFactory;

  protected final DividerFactory dividerFactory;

  protected final KeyComparatorFactory keyComparatorFactory;

  protected TreeNode rootNode = null;

  public RTree(int dimensions,
               int minSubNodes,
               int maxSubNodes,
               NodeFactory nodeFactory,
               DividerFactory dividerFactory,
               KeyComparatorFactory keyComparatorFactory) {
    this.dimensions = dimensions;
    this.minSubNodes = minSubNodes;
    this.maxSubNodes = maxSubNodes;
    this.nodeFactory = nodeFactory;
    this.dividerFactory = dividerFactory;
    this.keyComparatorFactory = keyComparatorFactory;
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

  TreeNode rootNode() {
    return rootNode;
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

    private TreeNode chooseNode(Set<TreeNode> nodes, Node nodeToInsert) {
      Comparator<SpatialKey> keyComparator = keyComparatorFactory.supplyComparator(nodeToInsert.spatialKey());
      Node chosenSubNode = nodes.stream()
          .min(Comparator.comparing(Node::spatialKey, keyComparator))
          .orElseThrow(() -> new IllegalStateException("No nodes present"));
      return (TreeNode) chosenSubNode;
    }

    private void replaceWithNodes(TreeNode node, TreeNode subNode, Set<TreeNode> newNodes) {
      node.removeSub(subNode);
      newNodes.forEach(node::addSubNode);
    }

    private Optional<Set<TreeNode>> splitIfNecessaryAndUpdateKey(TreeNode node) {
      Optional<Set<TreeNode>> split = new NodeSplitter().split(node);
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
          .map(Node::spatialKey)
          .collect(Collectors.reducing(SpatialKey::union))
          .get();
      node.spatialKey(unionKey);
    }

    private boolean subNodesAreLeaves(TreeNode node) {
      return node.subNodes().iterator().next() instanceof LeafNode;
    }

    private class NodeSplitter {

      public Optional<Set<TreeNode>> split(TreeNode node) {
        return Optional.ofNullable(isDividable(node) ? divide(node) : null);
      }

      private boolean isDividable(TreeNode node) {
        return node.numOfSubs() > maxSubNodes;
      }

      protected Set<TreeNode> divide(TreeNode node) {
        Set<SpatialKey> keys = dividerFactory.create(subNodeKeys(node))
            .divide(minSubNodes);
        return newNodes(keys, node);
      }

      private Set<SpatialKey> subNodeKeys(TreeNode node) {
        return node.subNodes().map(Node::spatialKey).collect(Collectors.toSet());
      }

      private Set<TreeNode> newNodes(Set<SpatialKey> keys, TreeNode node) {
        Preconditions.checkArgument(keys.size() != 0, "Error - split to zero new nodes");
        Multimap<SpatialKey, Node> keysToNodes = node.subNodes()
            .collect(ArrayListMultimap::create, (map, sub) -> chooseAndPut(keys, map, sub), Multimap::putAll);
        return keysToNodes.keySet().stream()
            .map(key -> getTreeNode(keysToNodes, key))
            .collect(Collectors.toSet());
      }

      private TreeNode getTreeNode(Multimap<SpatialKey, Node> keysToNodes, SpatialKey key) {
        TreeNode treeNode = nodeFactory.treeNode(key);
        Collection<Node> nodesForKey = keysToNodes.get(key);
        Preconditions.checkState(!nodesForKey.isEmpty());
        nodesForKey.forEach(treeNode::addSubNode);
        return treeNode;
      }

      private boolean chooseAndPut(Set<SpatialKey> keys, Multimap<SpatialKey, Node> map, Node sub) {
        return map.put(chooseKey(keys, sub.spatialKey()), sub);
      }

      private SpatialKey chooseKey(Set<SpatialKey> keys, SpatialKey spatialKey) {
        Comparator<SpatialKey> keyComparator = keyComparatorFactory.supplyComparator(spatialKey);
        return keys.stream().max(keyComparator).get();
      }

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
      if (node instanceof LeafNode) {
        result.add(((LeafNode<T>) node).data());
      } else {
        ((TreeNode) node).subNodes()
            .filter(condition)
            .forEach(this::searchSubNodes);
      }
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

    protected DividerFactory dividerFactory = UniformDivider::new;

    protected KeyComparatorFactory keyComparatorFactory = VolumeIncreaseKeyComparator::new;

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

    public Builder<T> divisionPerformerFactory(DividerFactory dividerFactory) {
      this.dividerFactory = dividerFactory;
      return this;
    }

    public Builder<T> nodeComparatorFactory(KeyComparatorFactory keyComparatorFactory) {
      this.keyComparatorFactory = keyComparatorFactory;
      return this;
    }

    public <D> Builder<D> dataType(Class<D> dataClass) {
      return (Builder<D>) this;
    }

    public RTree<T> create() {
      return new RTree<>(dimensions, minSubNodes, maxSubNodes, nodeFactory, dividerFactory, keyComparatorFactory);
    }

    private void checkMinMax(int minSubNodes, int maxSubNodes) {
      Preconditions.checkArgument(minSubNodes > 0, "Minimal num of nodes must be positive. Got %s", minSubNodes);
      Preconditions.checkArgument(maxSubNodes > minSubNodes, "Max must be grater then min");
    }
  }
}