package rtree;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BoundTest {

  private final SpatialKey.Bound bound = new SpatialKey.Bound(0, 10);

  @Test
  public void testIntersects() {
    assertThat(bound.intersects(new SpatialKey.Bound(1, 9))).isTrue();
    assertThat(bound.intersects(new SpatialKey.Bound(5, 5))).isTrue();
    assertThat(bound.intersects(new SpatialKey.Bound(-1, 1))).isTrue();
    assertThat(bound.intersects(new SpatialKey.Bound(9, 11))).isTrue();
    assertThat(bound.intersects(new SpatialKey.Bound(-1, 0))).isTrue();
    assertThat(bound.intersects(new SpatialKey.Bound(10, 11))).isTrue();
    assertThat(bound.intersects(new SpatialKey.Bound(11, 43))).isFalse();
    assertThat(bound.intersects(new SpatialKey.Bound(-23, -1))).isFalse();
  }
}