package rtree;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class OverflowSplitter implements NodeSplitter {

  protected final int minSubNodes;

  protected final int maxSubNodes;

  public OverflowSplitter(int minSubNodes, int maxSubNodes) {
    this.minSubNodes = minSubNodes;
    this.maxSubNodes = maxSubNodes;
  }

  @Override
  public Optional<Set<TreeNode>> split(TreeNode node) {
    return Optional.ofNullable(isSplittable(node) ? divideAndCollect(node) : null);
  }

  private Set<TreeNode> divideAndCollect(TreeNode node) {
    return divideSubNodes(node)
            .stream()
            .map(this::treeNode)
            .collect(Collectors.toSet());
  }

  private boolean isSplittable(TreeNode node) {
    return node.subNodes().size() > maxSubNodes;
  }

  protected abstract Set<Collection<Node>> divideSubNodes(TreeNode node);

  private TreeNode treeNode(Collection<Node> division) {
    TreeNode treeNode = new TreeNode(getKey(division));
    division.forEach(treeNode::addSubNode);
    return treeNode;
  }

  private SpatialKey getKey(Collection<Node> division) {
    return division.iterator().next().spatialKey();
  }
}
