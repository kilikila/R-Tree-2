package rtree;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import rtree.implementations.UniformDivisionPerformer;
import rtree.implementations.VolumeIncreaseNodeComparator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Benchmark {

  private static final int DATA_SIZE = 10000;

  private final Map<SpatialKey, Double> data;

  private final int dimensions = 2;

  private final List<BenchmarkSetup> setups;

  private final SpatialKey queryKey = SpatialKey.builder(dimensions)
      .setBound(0, -5, 5)
      .setBound(1, 0, 0)
      .create();

  public static void main(String[] args) {
    new Benchmark().run();
  }

  public Benchmark(BenchmarkSetup... setups) {
    this.setups = Lists.newArrayList(setups);
    SpatialKey boundingBox = SpatialKeyTest.zeroCentredCube(20, dimensions);
    data = RTreeTest.generateSyntheticData(SpatialKey::volume, DATA_SIZE, boundingBox);
  }

  private RTree<Double> constructTree() {
    return RTree.builder()
        .dimensions(dimensions)
        .divisionPerformerFactory(UniformDivisionPerformer::new)
        .nodeComparatorFactory(VolumeIncreaseNodeComparator::new)
        .dataType(Double.class)
        .create();
  }

  private void run() {
    for (int i = 0; i < 3; i++) {
      rawSearch();
      setups.stream().forEach((setup) ->
          testAndLog(setup.setupTitle, constructTree()));
    }
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

  private void testAndLog(String splitterName, RTree<Double> tree) {
    System.out.println("Testing " + splitterName);
    testInsert(tree);
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

    private BenchmarkSetup(String setupTitle) {
      this.setupTitle = setupTitle;
    }
  }
}
