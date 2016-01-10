package rtree;

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class RTreeTest extends DimensionalTest{

  private static int DATA_SIZE = 1000;

  private RTree<SpatialKey> tree;

  private SpatialKey boundingBox = SpatialKeyTest.cube(20, dimensions);

  public RTreeTest(final Integer dimensions) {
    super(dimensions);
  }

  @Before
  public void setUp() {
    tree = RTree.builder()
        .dimensions(dimensions)
        .nodeSplitter(this::trivialSplit)
        .dataType(SpatialKey.class)
        .create();
    Map<SpatialKey, SpatialKey> data = generateSyntheticData();
    data.forEach(tree::insert);
  }

  @Test
  public void testDimension() {
    assertThat(tree.dimensions()).isEqualTo(dimensions);
  }

  @Test
  public void testIntersection() {
    Set<SpatialKey> wholeDataSet = tree.intersection(boundingBox);
    assertThat(wholeDataSet).hasSize(DATA_SIZE);
    SpatialKey queryKey = randomSpatialKey();
    Set<SpatialKey> intersection = tree.intersection(queryKey);
    assertThat(wholeDataSet).containsAll(intersection);
    assertThat(intersection).are(new Condition<>(queryKey::intersects,
        "Intersection check for key %s", queryKey));
  }

  private Map<SpatialKey, SpatialKey> generateSyntheticData() {
    return IntStream.range(0, DATA_SIZE)
        .mapToObj(i -> randomSpatialKey())
        .collect(Collectors.toMap(key -> key, key -> key));
  }

  private SpatialKey randomSpatialKey() {
    return SpatialKeyTest.randomBox(boundingBox);
  }

  private Optional<Set<TreeNode>> trivialSplit(TreeNode node) {
    if (node.subNodes().size() > 5) {
      Set<TreeNode> split = divideSubNodes(node)
          .stream()
          .map(this::treeNode)
          .collect(Collectors.toSet());
      return Optional.of(split);
    } else {
      return Optional.empty();
    }
  }

  private TreeNode treeNode(Set<Node> division) {
    TreeNode treeNode = new TreeNode(getKey(division));
    division.forEach(treeNode::addSubNode);
    return treeNode;
  }

  private SpatialKey getKey(Set<Node> division) {
    return division.iterator().next().spatialKey();
  }

  private Set<Set<Node>> divideSubNodes(TreeNode node) {
    List<Node> nodes = node.subNodes().stream().sorted(Comparator.comparingDouble(this::boundMin)).collect(Collectors.toList());
    int halfSize = nodes.size() / 2;
    Set<Set<Node>> division = new HashSet<>(2);
    Set<Node> nodes1 = nodes.subList(0, halfSize).stream().collect(Collectors.toSet());
    Set<Node> nodes2 = nodes.subList(halfSize, nodes.size()).stream().collect(Collectors.toSet());
    division.add(nodes1);
    division.add(nodes2);
    return division;
  }

  private double boundMin(Node node) {
    return node.spatialKey().bound(0).min();
  }
}
