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
    return super.treeBuilder().persistent("test-page-file.txt");
  }
}
