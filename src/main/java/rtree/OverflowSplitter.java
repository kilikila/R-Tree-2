package rtree;

import com.google.common.base.Preconditions;

import java.util.Optional;
import java.util.Set;

public abstract class OverflowSplitter implements NodeSplitter {

  protected final NodeFactory nodeFactory;

  protected final int minSubNodes;

  protected final int maxSubNodes;

  public OverflowSplitter(NodeFactory nodeFactory, int minSubNodes, int maxSubNodes) {
    this.nodeFactory = nodeFactory;
    Preconditions.checkArgument(minSubNodes > 0,
        "Minimal num of nodes must be positive. Got %s", minSubNodes);
    Preconditions.checkArgument(maxSubNodes > minSubNodes, "Max must be grater then min");
    this.minSubNodes = minSubNodes;
    this.maxSubNodes = maxSubNodes;
  }

  @Override
  public Optional<Set<TreeNode>> split(TreeNode node) {
    return Optional.ofNullable(isDividable(node) ? divide(node) : null);
  }

  protected abstract Set<TreeNode> divide(TreeNode node);

  private boolean isDividable(TreeNode node) {
    return node.numOfSubs() > maxSubNodes;
  }
}
