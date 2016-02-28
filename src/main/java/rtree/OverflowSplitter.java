package rtree;

import com.google.common.base.Preconditions;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class OverflowSplitter implements NodeSplitter {

  protected final int minSubNodes;

  protected final int maxSubNodes;

  public OverflowSplitter(int minSubNodes, int maxSubNodes) {
    Preconditions.checkArgument(minSubNodes > 0,
        "Minimal num of nodes must be positive. Got %s", minSubNodes);
    Preconditions.checkArgument(maxSubNodes > minSubNodes, "Max must be grater then min");
    this.minSubNodes = minSubNodes;
    this.maxSubNodes = maxSubNodes;
  }

  @Override
  public Optional<Set<SpatialKey>> split(TreeNode node) {
    return Optional.ofNullable(isDividable(node) ? divide(subNodeKeys(node)) : null);
  }

  private Set<SpatialKey> subNodeKeys(TreeNode node) {
    return node.subNodes().map(Node::spatialKey).collect(Collectors.toSet());
  }

  protected abstract Set<SpatialKey> divide(Set<SpatialKey> subNodeKeys);

  private boolean isDividable(TreeNode node) {
    return node.numOfSubs() > maxSubNodes;
  }
}
