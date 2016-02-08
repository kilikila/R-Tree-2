package rtree;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class SubNodeSelectorTest {

  @Test
  public void testTrivialSelection() {
    SpatialKey cube1 = SpatialKeyTest.cube(1, 0, 0);
    SpatialKey cube2 = SpatialKeyTest.cube(1, 1, 0);
    SpatialKey cube3 = SpatialKeyTest.cube(1, 2, 0);
    SpatialKey cube4 = SpatialKeyTest.cube(1, 3, 0);
    SpatialKey cube5 = SpatialKeyTest.cube(1, 4, 0);
    SpatialKey union = cube1.union(cube2).union(cube3).union(cube4).union(cube5);
    TreeNode node = new TreeNode(union);
    node.addSubNode(new TreeNode(cube1));
    node.addSubNode(new TreeNode(cube2));
    node.addSubNode(new TreeNode(cube3));
    node.addSubNode(new TreeNode(cube4));
    node.addSubNode(new TreeNode(cube5));
    LeafNode<Object> leafNode = new LeafNode<>(inside(cube4), new Object());
    TreeNode chosen = getSelector().chooseSubNode(node, leafNode);
    assertThat(chosen.spatialKey()).isEqualTo(cube4);
  }

  @Test
  public void testSelectionWithoutIntersection() {
    SpatialKey cube1 = SpatialKeyTest.cube(6, 0, 1);
    SpatialKey cube2 = SpatialKeyTest.cube(2, 60, 0);
    SpatialKey union = cube1.union(cube2);
    TreeNode node = new TreeNode(union);
    TreeNode node1 = new TreeNode(cube1);
    TreeNode node2 = new TreeNode(cube2);
    node.addSubNode(node1);
    node.addSubNode(node2);
    LeafNode<Object> leafNode = new LeafNode<>(SpatialKeyTest.cube(1, 10, 1), new Object());
    TreeNode chosen = getSelector().chooseSubNode(node, leafNode);
    assertThat(chosen).isEqualTo(node1);
  }

  private SpatialKey inside(SpatialKey key) {
    List<SpatialKey.Bound> bounds = IntStream.range(0, key.dimensions())
        .mapToObj(key::bound)
        .map(this::boundCenter)
        .collect(Collectors.toList());
    return new SpatialKey(bounds);
  }

  private SpatialKey.Bound boundCenter(SpatialKey.Bound bound) {
    double length = bound.length();
    double center = bound.min() + length / 2;
    return new SpatialKey.Bound(center - length / 10, center + length / 10);
  }

  private SubNodeSelector getSelector() {
    return new MinimalMetricsSelector(new VolumeNodeComparator());
  }

}