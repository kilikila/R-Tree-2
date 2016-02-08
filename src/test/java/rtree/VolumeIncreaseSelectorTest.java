package rtree;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class VolumeIncreaseSelectorTest {

  @Test
  public void testSelectionOfTwo() {
    SpatialKey cube1 = SpatialKeyTest.cube(2, -1, 0);
    SpatialKey cube2 = SpatialKeyTest.cube(2, 1, 0);
    TreeNode node = new TreeNode(cube1.union(cube2));
    node.addSubNode(new TreeNode(cube1));
    node.addSubNode(new TreeNode(cube2));
    SpatialKey center = center(cube1);
    LeafNode<Object> leafNode = new LeafNode<>(center, new Object());
    TreeNode chosen = new VolumeIncreaseSelector().chooseSubNode(node, leafNode);
    assertThat(chosen.spatialKey().equals(cube1));
  }

  private SpatialKey center(SpatialKey key) {
    List<SpatialKey.Bound> bounds = IntStream.range(0, key.dimensions())
        .mapToObj(key::bound)
        .map(this::boundCenter)
        .collect(Collectors.toList());
    return new SpatialKey(bounds);
  }

  private SpatialKey.Bound boundCenter(SpatialKey.Bound bound) {
    double center = bound.min() + (bound.max() - bound.min()) / 2;
    return new SpatialKey.Bound(center - 0.1, center + 0.1);
  }

  private Node randomSubNode(TreeNode treeNode) {
    return treeNode.subNodes().stream().findAny().get();
  }

}