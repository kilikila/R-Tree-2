package rtree;

public class VolumeNodeComparator extends NodeComparator {

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
