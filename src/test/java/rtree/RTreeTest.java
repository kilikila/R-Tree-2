package rtree;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class RTreeTest extends DimensionalTest{

  private static int DATA_SIZE = 1000;

  private RTree<Object> tree;

  public RTreeTest(final Integer dimensions) {
    super(dimensions);
  }

  @Before
  public void setUp() {
    tree = RTree.builder().dimensions(dimensions).create();
    Map<SpatialKey, Object> data = generateSyntheticData();
    data.forEach(tree::insert);
  }

  @Test
  public void testDimension() {
    assertThat(tree.dimensions()).isEqualTo(dimensions);
  }

  @Test
  public void testIntersection() {

  }

  private Map<SpatialKey, Object> generateSyntheticData() {
    return IntStream.range(0, DATA_SIZE)
        .mapToObj(this::randomHyperBox)
        .collect(Collectors.toMap(box -> box, box -> 0));
  }

  private SpatialKey randomHyperBox(int seed) {
    return new SpatialKey();
  }
}
