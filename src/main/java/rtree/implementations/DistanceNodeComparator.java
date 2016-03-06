package rtree.implementations;

import rtree.Node;
import rtree.SpatialKey;
import rtree.factories.NodeComparatorFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DistanceNodeComparator implements Comparator<SpatialKey> {

  private final List<Double> center;

  public DistanceNodeComparator(SpatialKey nodeToInsert) {
    this.center = center(nodeToInsert);
  }

  @Override
  public int compare(SpatialKey o1, SpatialKey o2) {
    return Double.compare(distance(center(o1)), distance(center(o2)));
  }

  private double distance(List<Double> center) {
    return Math.sqrt(IntStream.range(0, center.size())
        .mapToObj(i -> center.get(i) - this.center.get(i))
        .mapToDouble(d -> Math.pow(d, 2))
        .sum());
  }

  private List<Double> center(SpatialKey key) {
    return key.bounds()
        .map(this::boundCenter)
        .collect(Collectors.toList());
  }

  private Double boundCenter(SpatialKey.Bound bound) {
    return bound.min() + (bound.max() - bound.min());
  }
}
