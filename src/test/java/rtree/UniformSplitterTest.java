package rtree;

public class UniformSplitterTest extends OverflowSplitterTest {

  public UniformSplitterTest(Integer dimensions) {
    super(dimensions);
  }

  @Override
  protected UniformSplitter supplySplitter() {
    return new UniformSplitter(minSubNodes, maxSubNodes);
  }
}
