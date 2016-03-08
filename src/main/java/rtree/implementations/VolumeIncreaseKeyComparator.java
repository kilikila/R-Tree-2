package rtree.implementations;

import rtree.Node;
import rtree.SpatialKey;

import java.util.Comparator;

public class VolumeIncreaseKeyComparator implements Comparator<SpatialKey> {

  private final SpatialKey keyToInsert;

  public VolumeIncreaseKeyComparator(SpatialKey keyToInsert) {
    this.keyToInsert = keyToInsert;
  }

  @Override
  public int compare(SpatialKey o1, SpatialKey o2) {
    return Double.compare(volumeIncrease(o1), volumeIncrease(o2));
  }

  private double volumeIncrease(SpatialKey key) {
    double volume = key.volume();
    double volumeWithLeaf = key.union(keyToInsert).volume();
    return volumeWithLeaf - volume;
  }
}
