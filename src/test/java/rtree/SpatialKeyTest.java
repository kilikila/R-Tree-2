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
    SpatialKey randomBox = randomBox(cube(10, dimensions), 1.0);
    assertThat(randomBox.dimensions()).isEqualTo(dimensions);
  }

  public static SpatialKey cube(double side, int dimensions) {
    double halfSide = side / 2;
    List<SpatialKey.Bound> bounds = IntStream.range(0, dimensions)
        .mapToObj(i -> new SpatialKey.Bound(-halfSide, halfSide))
        .collect(Collectors.toList());
    return new SpatialKey(bounds);
  }

  public static SpatialKey cube(double side, double... centerShifts) {
    double halfSide = side / 2;
    List<SpatialKey.Bound> bounds = IntStream.range(0, centerShifts.length)
        .mapToObj(i -> new SpatialKey.Bound(-halfSide + centerShifts[i], halfSide + centerShifts[i]))
        .collect(Collectors.toList());
    return new SpatialKey(bounds);
  }

  public static SpatialKey randomBox(SpatialKey boundingBox, double maxBoundLength) {
    List<SpatialKey.Bound> bounds = boundingBox.bounds()
        .map(bound -> randomBound(bound.min(), bound.max(), maxBoundLength))
        .collect(Collectors.toList());
    return new SpatialKey(bounds);
  }

  private static SpatialKey.Bound randomBound(double min, double max, double maxBoundLength) {
    double randMin = min + Math.random() * (max - min);
    double boundLength = Math.random() * (max - randMin);
    double randMax = randMin + (boundLength > maxBoundLength ? maxBoundLength : boundLength);
    return new SpatialKey.Bound(randMin, randMax);
  }
}
