package rtree;

import java.util.Comparator;

public interface NodeComparator extends Comparator<Node> {

  void setNodeToInsert(Node nodeToInsert);
}
