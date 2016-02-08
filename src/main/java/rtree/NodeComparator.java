package rtree;

import java.util.Comparator;

public abstract class NodeComparator implements Comparator<Node> {

  protected Node nodeToInsert;

  public void setNodeToInsert(Node nodeToInsert) {
    this.nodeToInsert = nodeToInsert;
  }
}
