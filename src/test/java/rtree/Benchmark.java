package rtree;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import rtree.implementations.DistanceNodeComparator;
import rtree.implementations.UniformDivider;
import rtree.implementations.VolumeIncreaseNodeComparator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Benchmark {

  private static final int DATA_SIZE = 30000;

  private final Map<SpatialKey, Double> data;

  private final int dimensions = 2;

  private final List<BenchmarkSetup> setups;

  private final SpatialKey queryKey = SpatialKey.builder(dimensions)
      .setBound(0, -5, 5)
      .setBound(1, 0, 0)
      .create();

  public static void main(String[] args) {
    RTree.Builder<Object> builder1 = RTree.builder()
        .divisionPerformerFactory(UniformDivider::new)
        .nodeComparatorFactory(VolumeIncreaseNodeComparator::new)
        .setMinMax(80, 200);
    RTree.Builder<Object> builder2 = RTree.builder()
        .divisionPerformerFactory(UniformDivider::new)
        .nodeComparatorFactory(VolumeIncreaseNodeComparator::new)
        .setMinMax(80, 200);
    new Benchmark(
        new BenchmarkSetup("1", builder1),
        new BenchmarkSetup("2", builder2)).run();
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
    new VisualizableRTree<Double>(tree).visualize();
    testSearch(tree);
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

    private BenchmarkSetup(String setupTitle, RTree.Builder builder) {
      this.setupTitle = setupTitle;
      this.builder = builder;
    }
  }
}
