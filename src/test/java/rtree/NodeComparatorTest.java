package rtree;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import rtree.factories.NodeComparatorFactory;
import rtree.implementations.DistanceNodeComparator;
import rtree.implementations.VolumeIncreaseNodeComparator;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class NodeComparatorTest {

  private final NodeComparatorFactory comparatorFactory;

  public NodeComparatorTest(NodeComparatorFactory comparatorFactory) {
    this.comparatorFactory = comparatorFactory;
  }

  @Test
  public void testTrivialSelection() {
    SpatialKey cube1 = SpatialKeyTest.cube(1, 0, 0);
    SpatialKey cube2 = SpatialKeyTest.cube(1, 1, 0);
    SpatialKey cube3 = SpatialKeyTest.cube(1, 2, 0);
    SpatialKey cube4 = SpatialKeyTest.cube(1, 3, 0);
    SpatialKey cube5 = SpatialKeyTest.cube(1, 4, 0);
    SpatialKey union = cube1.union(cube2).union(cube3).union(cube4).union(cube5);
    TreeNode node = new TreeNode.InMemory(union);
    node.addSubNode(new TreeNode.InMemory(cube1));
    node.addSubNode(new TreeNode.InMemory(cube2));
    node.addSubNode(new TreeNode.InMemory(cube3));
    node.addSubNode(new TreeNode.InMemory(cube4));
    node.addSubNode(new TreeNode.InMemory(cube5));
    LeafNode<Object> leafNode = new LeafNode.InMemory<>(inside(cube4), new Object());
    Comparator<SpatialKey> nodeComparator = comparatorFactory.supplyComparator(leafNode.spatialKey());
    SpatialKey chosen = node.subNodes()
        .map(Node::spatialKey)
        .min(nodeComparator)
        .get();
    assertThat(chosen).isEqualTo(cube4);
  }

  @Test
  public void testSelectionWithoutIntersection() {
    SpatialKey cube1 = SpatialKeyTest.cube(6, 0, 1);
    SpatialKey cube2 = SpatialKeyTest.cube(2, 60, 0);
    SpatialKey union = cube1.union(cube2);
    TreeNode node = new TreeNode.InMemory(union);
    TreeNode node1 = new TreeNode.InMemory(cube1);
    TreeNode node2 = new TreeNode.InMemory(cube2);
    node.addSubNode(node1);
    node.addSubNode(node2);
    LeafNode<Object> leafNode = new LeafNode.InMemory<>(SpatialKeyTest.cube(1, 10, 1), new Object());
    Comparator<SpatialKey> nodeComparator = comparatorFactory.supplyComparator(leafNode.spatialKey());
    SpatialKey chosen = node.subNodes()
        .map(Node::spatialKey)
        .min(nodeComparator)
        .get();
    assertThat(chosen).isEqualTo(cube1);
  }

  private SpatialKey inside(SpatialKey key) {
    List<SpatialKey.Bound> bounds = key.bounds()
        .map(this::boundCenter)
        .collect(Collectors.toList());
    return new SpatialKey(bounds);
  }

  private SpatialKey.Bound boundCenter(SpatialKey.Bound bound) {
    double length = bound.length();
    double center = bound.min() + length / 2;
    return new SpatialKey.Bound(center - length / 10, center + length / 10);
  }

  @Parameterized.Parameters
  public static Set<NodeComparatorFactory> comparatorFactories() {
    return Sets.newHashSet(VolumeIncreaseNodeComparator::new, DistanceNodeComparator::new);
  }
}