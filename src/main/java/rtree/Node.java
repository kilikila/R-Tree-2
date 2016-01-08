package rtree;

public abstract class Node {
  private final SpatialKey key;

  public Node(SpatialKey key) {
    this.key = key;
  }

  public SpatialKey spatialKey() {
    return key;
  }
}
