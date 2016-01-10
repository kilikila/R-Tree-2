package rtree;

import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class OverflowSplitterTest extends SplitterTest {

  protected final int minSubNodes = 4;

  protected final int maxSubNodes = 10;

  public OverflowSplitterTest(Integer dimensions) {
    super(dimensions);
  }

  @Test
  public void testSplitContainsExactlyMin() {
    int splitSize = supplySplitter().split(nodeToSplit).get().size();
    assertThat(splitSize).isEqualTo(minSubNodes);
  }

  @Override
  protected abstract OverflowSplitter supplySplitter();

  @Override
  protected void addSubNodesEnoughToSplit() {
    Set<TreeNode> nodes = IntStream.range(0, maxSubNodes + 1)
        .mapToObj(i -> new TreeNode(randomKey()))
        .collect(Collectors.toSet());
    nodes.forEach(nodeToSplit::addSubNode);
  }

  private SpatialKey randomKey() {
    return SpatialKeyTest.randomBox(nodeToSplit.spatialKey());
  }
}
