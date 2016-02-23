package rtree;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public interface TreeNode extends Node {

  Stream<Node> subNodes();

  int numOfSubs();

  void addSubNode(Node subNode);

  void removeSub(TreeNode subNode);

  class InMemory extends Node.InMemory implements TreeNode {

    private final Set<Node> subNodes = new HashSet<>();

    public InMemory(SpatialKey key) {
      super(key);
    }

    @Override
    public Stream<Node> subNodes() {
      return subNodes.stream();
    }

    @Override
    public int numOfSubs() {
      return subNodes.size();
    }

    @Override
    public void addSubNode(Node subNode) {
      subNodes.add(subNode);
    }

    @Override
    public void removeSub(TreeNode subNode) {
      subNodes.remove(subNode);
    }
  }
}
