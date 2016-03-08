package rtree;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import rtree.factories.KeyComparatorFactory;
import rtree.implementations.CombiningComparator;
import rtree.implementations.UniformKeyDivider;
import rtree.implementations.VolumeIncreaseKeyComparator;
import rtree.persistent.PageFile;
import rtree.persistent.PersistentRTree;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Benchmark {

  private static final int DATA_SIZE = 20;

  private final Map<SpatialKey, Double> data;

  private final int dimensions = 2;

  private final List<BenchmarkSetup> setups;

  private final SpatialKey queryKey = SpatialKey.builder()
      .setBound(-5, 5)
      .setBound(0, 0)
      .create();

  public static void main(String[] args) {
    RTree.Builder<Object> builder1 = RTree.builder()
        .divisionPerformerFactory(UniformKeyDivider::new)
        .nodeComparatorFactory(VolumeIncreaseKeyComparator::new)
        .setMinMax(40, 100);
    KeyComparatorFactory factory = CombiningComparator
        .factory(VolumeIncreaseKeyComparator::new);
    RTree.Builder<Object> builder2 = PersistentRTree.builder(new PageFile("test-page-file.txt", 20000))
        .divisionPerformerFactory(UniformKeyDivider::new)
        .nodeComparatorFactory(factory)
        .setMinMax(4, 10);
    new Benchmark(
//        new BenchmarkSetup("1", builder1, false),
        new BenchmarkSetup("2", builder2, true)
    ).run();
  }

  public Benchmark(BenchmarkSetup... setups) {
    this.setups = Lists.newArrayList(setups);
    SpatialKey boundingBox = SpatialKeyTest.zeroCentredCube(20, dimensions);
    data = RTreeTest.generateSyntheticData(SpatialKey::volume, DATA_SIZE, boundingBox);
  }

  private RTree<Double> constructTree(RTree.Builder<?> builder) {
    return builder
        .dimensions(dimensions)
        .dataType(Double.class)
        .create();
  }

  private void run() {
    rawSearch();
    setups.stream().forEach(this::testAndLog);
  }

  private void rawSearch() {
    System.out.println("Searching in raw data (" + data.size() + " entries)");
    Stopwatch stopwatch = Stopwatch.createStarted();
    Set<Double> result = data.entrySet()
        .stream()
        .filter(entry -> entry.getKey().intersects(queryKey))
        .map(Map.Entry::getValue)
        .collect(Collectors.toSet());
    System.out.println("Search performed. " + result.size() + " items found. Elapsed time: " + stopwatch);
  }

  private void testAndLog(BenchmarkSetup setup) {
    System.out.println("Testing " + setup.setupTitle);
    RTree<Double> tree = constructTree(setup.builder);
    testInsert(tree);
    testSearch(tree);
    if (setup.visualize) new TreeVisualizer(tree.rootNode(), 40, false).visualize();
  }

  private void testInsert(RTree<Double> tree) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    data.forEach(tree::insert);
    System.out.println("Inserted data. Elapsed time: " + stopwatch);
  }

  private void testSearch(RTree<Double> tree) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    Set<Double> result = tree.intersection(queryKey);
    System.out.println("Search performed. " + result.size() + " items found. Elapsed time: " + stopwatch);
  }

  private static class BenchmarkSetup {

    private final String setupTitle;

    private final RTree.Builder<?> builder;

    public final boolean visualize;

    private BenchmarkSetup(String setupTitle, RTree.Builder builder, boolean visualize) {
      this.setupTitle = setupTitle;
      this.builder = builder;
      this.visualize = visualize;
    }
  }
}
