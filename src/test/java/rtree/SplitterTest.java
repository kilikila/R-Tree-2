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
    nodeToSplit = new TreeNode.InMemory(SpatialKeyTest.cube(20, dimensions));
  }

  @Test
  public void testSplit() {
    TreeNode nodeToSplit = this.nodeToSplit;
    Set<TreeNode> newNodes = performSplitAndTest(nodeToSplit);
    TreeNode node = newNodes.iterator().next();
    performSplitAndTest(node);
  }

  private Set<TreeNode> performSplitAndTest(TreeNode nodeToSplit) {
    addSubNodesEnoughToSplit(nodeToSplit);
    NodeSplitter splitter = supplySplitter();
    Optional<Set<TreeNode>> split = splitter.split(nodeToSplit);
    assertThat(split.isPresent()).isTrue();
    Set<TreeNode> newNodes = split.get();
    assertThat(numberOfSubNodes(newNodes)).isEqualTo(nodeToSplit.numOfSubs());
    Set<Node> allSubNodes = newNodes.stream()
        .flatMap(TreeNode::subNodes)
        .collect(Collectors.toSet());
    assertThat(allSubNodes).containsAll(nodeToSplit.subNodes().collect(Collectors.toSet()));
    assertThat(allSubNodes).containsOnlyElementsOf(nodeToSplit.subNodes().collect(Collectors.toSet()));
    return newNodes;
  }

  private int numberOfSubNodes(Set<TreeNode> newNodes) {
    return newNodes.stream().mapToInt(TreeNode::numOfSubs).sum();
  }

  protected abstract NodeSplitter supplySplitter();

  protected abstract void addSubNodesEnoughToSplit(TreeNode nodeToSplit);
}
