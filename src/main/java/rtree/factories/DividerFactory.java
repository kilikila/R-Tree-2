package rtree.factories;

import rtree.Divider;
import rtree.SpatialKey;

import java.util.Set;

public interface DividerFactory {

  Divider create(Set<SpatialKey> subNodeKeys);
}
