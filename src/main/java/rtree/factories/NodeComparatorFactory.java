package rtree.factories;

import rtree.Node;

import java.util.Comparator;

public interface NodeComparatorFactory {

  Comparator<Node> supplyComparator(Node nodeToInsert);

}
