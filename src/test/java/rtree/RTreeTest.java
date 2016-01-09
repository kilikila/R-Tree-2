package rtree;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class RTreeTest extends DimensionalTest{

  private static int DATA_SIZE = 1000;

  private RTree<SpatialKey, SpatialKey> tree;

  private SpatialKey boundingBox = SpatialKeyTest.cube(20, dimensions);

  public RTreeTest(final Integer dimensions) {
    super(dimensions);
  }

  @Before
  public void setUp() {
    tree = RTree.builder()
        .dimensions(dimensions)
        .spatialKey(SpatialKey.class)
        .dataType(SpatialKey.class)
        .create();
    Map<SpatialKey, SpatialKey> data = generateSyntheticData();
    data.forEach(tree::insert);
  }

  @Test
  public void testDimension() {
    assertThat(tree.dimensions()).isEqualTo(dimensions);
  }

  @Test
  public void testIntersection() {
    Set<SpatialKey> intersection = tree.intersection(boundingBox);
    assertThat(intersection).hasSize(DATA_SIZE);
  }

  private Map<SpatialKey, SpatialKey> generateSyntheticData() {
    return IntStream.range(0, DATA_SIZE)
        .mapToObj(i -> randomSpatialKey())
        .collect(Collectors.toMap(key -> key, key -> key));
  }

  private SpatialKey randomSpatialKey() {
    return SpatialKeyTest.randomBox(boundingBox);
  }
}
