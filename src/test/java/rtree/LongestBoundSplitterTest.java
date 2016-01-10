package rtree;

public class LongestBoundSplitterTest extends MinMaxSplitterTest {

  public LongestBoundSplitterTest(Integer dimensions) {
    super(dimensions);
  }

  @Override
  protected LongestBoundSplitter supplySplitter() {
    return new LongestBoundSplitter(minSubNodes, maxSubNodes);
  }
}
