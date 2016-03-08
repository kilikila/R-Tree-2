package rtree.factories;

import rtree.KeyDivider;
import rtree.SpatialKey;

import java.util.Set;

public interface KeyDividerFactory {

  KeyDivider create(Set<SpatialKey> subNodeKeys);
}
