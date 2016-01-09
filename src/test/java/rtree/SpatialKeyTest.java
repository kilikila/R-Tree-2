package rtree;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class SpatialKeyTest extends DimensionalTest {

  public SpatialKeyTest(Integer dimensions) {
    super(dimensions);
  }

  @Test
  public void testNew() {
    SpatialKey randomBox = randomBox(cube(10, dimensions));
    assertThat(randomBox.dimensions()).isEqualTo(dimensions);
  }

  public static SpatialKey cube(double side, int dimensions) {
    double halfSide = side / 2;
    List<SpatialKey.BoxBound> bounds = IntStream.range(0, dimensions)
        .mapToObj(i -> new SpatialKey.BoxBound(-halfSide, halfSide))
        .collect(Collectors.toList());
    return new SpatialKey(bounds);
  }

  public static SpatialKey randomBox(SpatialKey boundingBox) {
    List<SpatialKey.BoxBound> bounds = IntStream.range(0, boundingBox.dimensions())
        .mapToObj(boundingBox::bound)
        .map(bound -> randomBound(bound.min(), bound.max()))
        .collect(Collectors.toList());
    return new SpatialKey(bounds);
  }

  private static SpatialKey.BoxBound randomBound(double min, double max) {
    double randMin = min + Math.random() * (max - min);
    double randMax = randMin + Math.random() * (max - randMin);
    return new SpatialKey.BoxBound(randMin, randMax);
  }
}
