package rtree;

import org.assertj.core.util.Lists;
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
    SpatialKey randomBox = randomBox(zeroCentredCube(10, dimensions), 1.0);
    assertThat(randomBox.dimensions()).isEqualTo(dimensions);
  }

  @Test
  public void testUnion() {
    SpatialKey cube1 = cube(-4, -2);
    SpatialKey cube2 = cube(-2, 0);
    SpatialKey cube3 = cube(0, 2);
    SpatialKey cube4 = cube(2, 4);

    SpatialKey unionAll = SpatialKey.union(Lists.newArrayList(cube1, cube2, cube3, cube4));
    assertThat(unionAll).isEqualTo(zeroCentredCube(8, dimensions));

    SpatialKey unionFirstTwo = SpatialKey.union(Lists.newArrayList(cube1, cube2));
    assertThat(unionFirstTwo).isEqualTo(cube(-4, 0));

    SpatialKey unionLastTwo = SpatialKey.union(Lists.newArrayList(cube3, cube4));
    assertThat(unionLastTwo).isEqualTo(cube(0, 4));
  }

  private SpatialKey cube(double min, double max) {
    SpatialKey.Builder builder = SpatialKey.builder();
    IntStream.range(0, dimensions)
        .forEach(i -> builder.setBound(min, max));
    return builder.create();
  }

  public static SpatialKey zeroCentredCube(double side, int dimensions) {
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
