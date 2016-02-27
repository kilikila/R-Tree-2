package rtree;

public class LongestBoundSplitterTest extends OverflowSplitterTest {

  public LongestBoundSplitterTest(Integer dimensions) {
    super(dimensions);
  }

  @Override
  protected LongestBoundSplitter supplySplitter() {
    return new LongestBoundSplitter(NodeFactory.inMemory(), minSubNodes, maxSubNodes);
  }
}
