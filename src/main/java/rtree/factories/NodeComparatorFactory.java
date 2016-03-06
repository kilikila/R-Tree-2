package rtree.factories;

import rtree.Node;
import rtree.SpatialKey;

import java.util.Comparator;

public interface NodeComparatorFactory {

  Comparator<SpatialKey> supplyComparator(SpatialKey nodeToInsert);

}
