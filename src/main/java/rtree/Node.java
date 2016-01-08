package rtree;

public abstract class Node {
  private SpatialKey key;

  public Node(SpatialKey key) {
    this.key = key;
  }

  public SpatialKey spatialKey() {
    return key;
  }

  public void spatialKey(SpatialKey key) {
    this.key = key;
  }
}
