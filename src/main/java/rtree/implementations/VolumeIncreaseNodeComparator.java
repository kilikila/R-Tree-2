package rtree.implementations;

import rtree.Node;

import java.util.Comparator;

public class VolumeIncreaseNodeComparator implements Comparator<Node> {

  private final Node nodeToInsert;

  public VolumeIncreaseNodeComparator(Node nodeToInsert) {
    this.nodeToInsert = nodeToInsert;
  }

  @Override
  public int compare(Node o1, Node o2) {
    return Double.compare(volumeIncrease(o1), volumeIncrease(o2));
  }

  private double volumeIncrease(Node node) {
    double volume = node.spatialKey().volume();
    double volumeWithLeaf = node.spatialKey().union(nodeToInsert.spatialKey()).volume();
    return volumeWithLeaf - volume;
  }
}
