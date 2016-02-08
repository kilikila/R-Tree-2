package rtree;

public class VolumeIncreaseSelector extends MinimalMetricsSelector {

  @Override
  protected NodeComparator supplyComparator(LeafNode<?> leafNode) {
    return new VolumeNodeComparator(leafNode);
  }

  private static class VolumeNodeComparator extends NodeComparator {

    public VolumeNodeComparator(LeafNode<?> leafNode) {
      super(leafNode);
    }

    @Override
    public int compare(Node o1, Node o2) {
      return Double.compare(volumeIncrease(o1), volumeIncrease(o2));
    }

    private double volumeIncrease(Node node) {
      double volume = node.spatialKey().volume();
      double volumeWithLeaf = node.spatialKey().union(leafNode.spatialKey()).volume();
      return volumeWithLeaf - volume;
    }
  }
}
