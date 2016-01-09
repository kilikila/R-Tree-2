package rtree;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BoxBoundTest {

  private final SpatialKey.BoxBound bound = new SpatialKey.BoxBound(0, 10);

  @Test
  public void testIntersects() {
    assertThat(bound.intersects(new SpatialKey.BoxBound(1, 9))).isTrue();
    assertThat(bound.intersects(new SpatialKey.BoxBound(5, 5))).isTrue();
    assertThat(bound.intersects(new SpatialKey.BoxBound(-1, 1))).isTrue();
    assertThat(bound.intersects(new SpatialKey.BoxBound(9, 11))).isTrue();
    assertThat(bound.intersects(new SpatialKey.BoxBound(-1, 0))).isTrue();
    assertThat(bound.intersects(new SpatialKey.BoxBound(10, 11))).isTrue();
    assertThat(bound.intersects(new SpatialKey.BoxBound(11, 43))).isFalse();
    assertThat(bound.intersects(new SpatialKey.BoxBound(-23, -1))).isFalse();
  }
}