package rtree;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HyperBoxTest extends DimensionalTest{

  public HyperBoxTest(Integer dimensions) {
    super(dimensions);
  }

  @Test
  public void testNew() {
    HyperBox randomBox = createRandomBox(dimensions);
  }

  public static HyperBox createRandomBox(int dimensions) {
    List<HyperBox.Bound> bounds = IntStream.range(0, dimensions)
        .mapToObj((i) -> HyperBoxTest.randomBound(-10.0, 10.0))
        .collect(Collectors.toList());
    return new HyperBox(bounds);
  }

  private static HyperBox.Bound randomBound(double min, double max) {
    double randMin = min + Math.random() * (max - min);
    double randMax = randMin + Math.random() * (max - randMin);
    return new HyperBox.Bound(randMin, randMax);
  }
}
