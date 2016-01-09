package rtree;

import java.util.Optional;
import java.util.Set;

public class LinearSplitter implements NodeSplitter {

  private final int minSubNodes;

  private final int maxSubNodes;

  public LinearSplitter(int minSubNodes, int maxSubNodes) {
    this.minSubNodes = minSubNodes;
    this.maxSubNodes = maxSubNodes;
  }

  @Override
  public Optional<Set<TreeNode>> split(TreeNode node) {
    return null;
  }
}
