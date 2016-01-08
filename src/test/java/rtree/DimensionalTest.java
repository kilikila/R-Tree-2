package rtree;


import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public abstract class DimensionalTest {

  private static int MAX_DIMENSION = 5;

  protected final int dimensions;

  public DimensionalTest(final Integer dimensions) {
    this.dimensions = dimensions;
  }

  @Parameterized.Parameters
  public static Set<Integer> dimensions() {
    return IntStream.range(1, MAX_DIMENSION + 1).boxed().collect(Collectors.toSet());
  }

}
