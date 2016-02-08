package rtree;

import java.util.Comparator;

public abstract class NodeComparator implements Comparator<Node> {

  protected final LeafNode<?> leafNode;

  public NodeComparator(LeafNode<?> leafNode) {
    this.leafNode = leafNode;
  }
}
