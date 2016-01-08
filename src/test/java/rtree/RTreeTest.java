package rtree;

import org.junit.Before;
import org.junit.Test;
import rtree.key.HyperBox;
import rtree.key.HyperBoxTest;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class RTreeTest extends DimensionalTest{

  private static int DATA_SIZE = 1000;

  private RTree<HyperBox, HyperBox> tree;

  private HyperBox boundingBox = HyperBoxTest.cube(20, dimensions);

  public RTreeTest(final Integer dimensions) {
    super(dimensions);
  }

  @Before
  public void setUp() {
    tree = RTree.builder()
        .dimensions(dimensions)
        .spatialKey(HyperBox.class)
        .dataType(HyperBox.class)
        .create();
    Map<HyperBox, HyperBox> data = generateSyntheticData();
    data.forEach(tree::insert);
  }

  @Test
  public void testDimension() {
    assertThat(tree.dimensions()).isEqualTo(dimensions);
  }

  @Test
  public void testIntersection() {
    Set<HyperBox> intersection = tree.search(boundingBox);
    assertThat(intersection).hasSize(DATA_SIZE);
  }

  private Map<HyperBox, HyperBox> generateSyntheticData() {
    return IntStream.range(0, DATA_SIZE)
        .mapToObj((seed) -> randomSpatialKey())
        .collect(Collectors.toMap(key -> key, key -> key));
  }

  private HyperBox randomSpatialKey() {
    return HyperBoxTest.randomBox(boundingBox);
  }
}
