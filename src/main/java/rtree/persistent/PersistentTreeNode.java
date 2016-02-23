package rtree.persistent;

import rtree.Node;
import rtree.SpatialKey;
import rtree.TreeNode;

import java.util.stream.Stream;

public class PersistentTreeNode extends PersistentNode implements TreeNode {

  public PersistentTreeNode(SpatialKey key) {
  }

  @Override
  public Stream<Node> subNodes() {
    return null;
  }

  @Override
  public int numOfSubs() {
    return 0;
  }

  @Override
  public void addSubNode(Node subNode) {

  }

  @Override
  public void removeSub(TreeNode subNode) {

  }
}
