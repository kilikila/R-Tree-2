package rtree.persistent;

import rtree.RTree;
import rtree.RTreeTest;
import rtree.SpatialKey;

public class PersistentRTreeTest extends RTreeTest {

  public PersistentRTreeTest(Integer dimensions) {
    super(dimensions);
  }

  @Override
  protected RTree.Builder<SpatialKey> treeBuilder() {
    return PersistentRTree.builder(new PageFile("test-page-file.txt", 20000))
        .dimensions(dimensions)
        .dataType(SpatialKey.class);
  }
}
