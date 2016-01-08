package rtree;

import java.util.HashSet;
import java.util.Set;

public class TreeNode extends Node {

  private final Set<Node> subNodes = new HashSet<>();

  public TreeNode(HyperBox box) {
    super(box);
  }

  public Set<Node> subNodes() {
    return subNodes;
  }
}
