package rtree.persistent;

import org.junit.Test;
import rtree.SpatialKey;
import rtree.SpatialKeyTest;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class PersistentNodeTest {

  @Test
  public void testSpatialKey() {
    SpatialKey cube = SpatialKeyTest.zeroCentredCube(10, 3);
    PersistentNode node = supplyPersistentNode(cube);
    SpatialKey spatialKey = node.spatialKey();
    assertThat(spatialKey).isEqualTo(cube);
  }

  protected abstract PersistentNode supplyPersistentNode(SpatialKey spatialKey);

}
