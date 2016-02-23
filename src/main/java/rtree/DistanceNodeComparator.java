package rtree;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DistanceNodeComparator implements NodeComparator {

  List<Double> center;

  @Override
  public void setNodeToInsert(Node nodeToInsert) {
    center = center(nodeToInsert);
  }

  @Override
  public int compare(Node o1, Node o2) {
    return Double.compare(distance(center(o1)), distance(center(o2)));
  }

  private double distance(List<Double> center) {
    return Math.sqrt(IntStream.range(0, center.size())
        .mapToObj(i -> center.get(i) - this.center.get(i))
        .mapToDouble(l -> Math.pow(l, 2))
        .sum());
  }

  private List<Double> center(Node nodeToInsert) {
    SpatialKey key = nodeToInsert.spatialKey();
    return IntStream.range(0, key.dimensions())
        .mapToObj(key::bound)
        .map(this::boundCenter)
        .collect(Collectors.toList());
  }

  private Double boundCenter(SpatialKey.Bound bound) {
    return bound.min() + (bound.max() - bound.min());
  }
}
