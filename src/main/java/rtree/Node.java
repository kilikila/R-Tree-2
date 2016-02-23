package rtree;

public interface Node {

  SpatialKey spatialKey();

  void spatialKey(SpatialKey key);

  abstract class InMemory implements Node {
    private SpatialKey key;

    public InMemory(SpatialKey key) {
      this.key = key;
    }

    @Override
    public SpatialKey spatialKey() {
      return key;
    }

    @Override
    public void spatialKey(SpatialKey key) {
      this.key = key;
    }
  }
}
