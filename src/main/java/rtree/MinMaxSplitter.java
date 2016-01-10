package rtree;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MinMaxSplitter implements NodeSplitter {

  private final int minSubNodes;

  private final int maxSubNodes;

  public MinMaxSplitter(int minSubNodes, int maxSubNodes) {
    this.minSubNodes = minSubNodes;
    this.maxSubNodes = maxSubNodes;
  }

  @Override
  public Optional<Set<TreeNode>> split(TreeNode node) {
    if (node.subNodes().size() > maxSubNodes) {
      Set<TreeNode> split = divideSubNodes(node)
          .stream()
          .map(this::treeNode)
          .collect(Collectors.toSet());
      return Optional.of(split);
    } else {
      return Optional.empty();
    }
  }

  protected abstract Set<Set<Node>> divideSubNodes(TreeNode node);

  private TreeNode treeNode(Set<Node> division) {
    TreeNode treeNode = new TreeNode(getKey(division));
    division.forEach(treeNode::addSubNode);
    return treeNode;
  }

  private SpatialKey getKey(Set<Node> division) {
    return division.iterator().next().spatialKey();
  }
}
