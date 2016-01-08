package rtree.key;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BoxBoundTest {

  private final BoxBound bound = new BoxBound(0, 10);

  @Test
  public void testIntersects() {
    assertThat(bound.intersects(new BoxBound(1, 9))).isTrue();
    assertThat(bound.intersects(new BoxBound(5, 5))).isTrue();
    assertThat(bound.intersects(new BoxBound(-1, 1))).isTrue();
    assertThat(bound.intersects(new BoxBound(9, 11))).isTrue();
    assertThat(bound.intersects(new BoxBound(-1, 0))).isTrue();
    assertThat(bound.intersects(new BoxBound(10, 11))).isTrue();
    assertThat(bound.intersects(new BoxBound(11, 43))).isFalse();
    assertThat(bound.intersects(new BoxBound(-23, -1))).isFalse();
  }
}