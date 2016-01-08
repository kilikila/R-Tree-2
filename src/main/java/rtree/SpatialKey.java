package rtree;

public interface SpatialKey {

  int dimensions();

  boolean intersects(SpatialKey spatialKey);

  SpatialKey union(SpatialKey key);
}
