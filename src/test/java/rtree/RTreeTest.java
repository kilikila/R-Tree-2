package rtree;

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class RTreeTest extends DimensionalTest {

  private static int DATA_SIZE = 1000;

  private RTree<SpatialKey> tree;

  private SpatialKey boundingBox = SpatialKeyTest.cube(20, dimensions);

  public RTreeTest(final Integer dimensions) {
    super(dimensions);
  }

  @Before
  public void setUp() {
    tree = RTree.builder()
        .dimensions(dimensions)
        .nodeSplitter(new LongestBoundSplitter(4, 10))
        .dataType(SpatialKey.class)
        .create();
    Map<SpatialKey, SpatialKey> data = generateSyntheticData(key -> key, DATA_SIZE, boundingBox);
    data.forEach(tree::insert);
  }

  @Test
  public void testDimension() {
    assertThat(tree.dimensions()).isEqualTo(dimensions);
  }

  @Test
  public void testIntersection() {
    Set<SpatialKey> wholeDataSet = tree.intersection(boundingBox);
    assertThat(wholeDataSet).hasSize(DATA_SIZE);
    SpatialKey queryKey = SpatialKeyTest.randomBox(boundingBox);
    Set<SpatialKey> intersection = tree.intersection(queryKey);
    assertThat(wholeDataSet).containsAll(intersection);
    assertThat(intersection).are(new Condition<>(queryKey::intersects,
        "Intersection check for key %s", queryKey));
  }

  static <T> Map<SpatialKey, T> generateSyntheticData(
      Function<SpatialKey, T> dataExtractor,
      int dataSize,
      SpatialKey boundingBox) {
    return IntStream.range(0, dataSize)
        .mapToObj(i -> SpatialKeyTest.randomBox(boundingBox))
        .collect(Collectors.toMap(key -> key, dataExtractor));
  }

}
