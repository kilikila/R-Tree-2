package rtree;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class SplitterTest extends DimensionalTest{

  protected TreeNode nodeToSplit;

  public SplitterTest(Integer dimensions) {
    super(dimensions);
  }

  @Before
  public void setUp() {
    nodeToSplit = new TreeNode(SpatialKeyTest.cube(20, dimensions));
    addSubNodesEnoughToSplit();
  }

  @Test
  public void testSplit() {
    NodeSplitter splitter = supplySplitter();
    Optional<Set<TreeNode>> split = splitter.split(nodeToSplit);
    assertThat(split.isPresent()).isTrue();
    Set<TreeNode> newNodes = split.get();
    assertThat(numberOfSubNodes(newNodes)).isEqualTo(nodeToSplit.subNodes().size());
    Set<Node> allSubNodes = newNodes.stream()
        .flatMap(node -> node.subNodes().stream())
        .collect(Collectors.toSet());
    assertThat(allSubNodes).containsAll(nodeToSplit.subNodes());
    assertThat(allSubNodes).containsOnlyElementsOf(nodeToSplit.subNodes());
  }

  private int numberOfSubNodes(Set<TreeNode> newNodes) {
    return newNodes.stream().map(TreeNode::subNodes).mapToInt(Set::size).sum();
  }

  protected abstract NodeSplitter supplySplitter();

  protected abstract void addSubNodesEnoughToSplit();
}
