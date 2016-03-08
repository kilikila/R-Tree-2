package rtree.factories;

import rtree.Node;
import rtree.SpatialKey;

import java.util.Comparator;

public interface KeyComparatorFactory {

  Comparator<SpatialKey> supplyComparator(SpatialKey keyToInsert);

}
