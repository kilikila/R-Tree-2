package rtree.factories;

import rtree.DivisionPerformer;
import rtree.SpatialKey;

import java.util.Set;

public interface DivisionPerformerFactory {

  DivisionPerformer create(Set<SpatialKey> subNodeKeys);
}
