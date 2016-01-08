package rtree;

import java.util.HashSet;
import java.util.Set;

public class TreeNode extends Node {

  private final Set<Node> subNodes = new HashSet<>();

  public TreeNode(SpatialKey key) {
    super(key);
  }

  public Set<Node> subNodes() {
    return subNodes;
  }

  public void addSubNode(Node subNode) {
    subNodes.add(subNode);
  }
}
