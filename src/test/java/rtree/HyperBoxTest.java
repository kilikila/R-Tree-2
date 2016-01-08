package rtree;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class HyperBoxTest extends DimensionalTest{

  public HyperBoxTest(Integer dimensions) {
    super(dimensions);
  }

  @Test
  public void testNew() {
    HyperBox randomBox = randomBox(cube(10, dimensions));
    assertThat(randomBox.dimensions()).isEqualTo(dimensions);
  }

  public static HyperBox cube(double side, int dimensions) {
    double halfSide = side / 2;
    List<BoxBound> bounds = IntStream.range(0, dimensions)
        .mapToObj(i -> new BoxBound(-halfSide, halfSide))
        .collect(Collectors.toList());
    return new HyperBox(bounds);
  }

  public static HyperBox randomBox(HyperBox boundingBox) {
    List<BoxBound> bounds = IntStream.range(0, boundingBox.dimensions())
        .mapToObj(boundingBox::bound)
        .map(bound -> randomBound(bound.min(), bound.max()))
        .collect(Collectors.toList());
    return new HyperBox(bounds);
  }

  private static BoxBound randomBound(double min, double max) {
    double randMin = min + Math.random() * (max - min);
    double randMax = randMin + Math.random() * (max - randMin);
    return new BoxBound(randMin, randMax);
  }
}
