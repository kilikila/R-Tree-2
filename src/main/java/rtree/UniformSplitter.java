package rtree;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UniformSplitter extends OverflowSplitter {

  public UniformSplitter(int minSubNodes, int maxSubNodes) {
    super(minSubNodes, maxSubNodes);
  }

  protected Set<SpatialKey> divide(Set<SpatialKey> subNodeKeys) {
    return new DivisionPerformer(subNodeKeys).divide();
  }

  private class DivisionPerformer {

    private final SpatialKey boundingKey;

    public DivisionPerformer(Set<SpatialKey> subKeys) {
      boundingKey = SpatialKey.union(subKeys);
    }

    public Set<SpatialKey> divide() {
      double splitsPerDim = Math.pow(minSubNodes, boundingKey.dimensions());
      double averageBoundLength = boundingKey.bounds()
          .mapToDouble(SpatialKey.Bound::length)
          .average().getAsDouble();
      List<Set<SpatialKey.Bound>> newBounds = boundingKey.bounds()
          .map(bound -> splitBound(bound, splitsPerDim, averageBoundLength))
          .collect(Collectors.toList());
      return new BoundsCombiner(newBounds).combine();
    }

    private Set<SpatialKey.Bound> splitBound(SpatialKey.Bound bound, double splitsPerDim, double averageBoundLength) {
      double length = bound.length();
      int splitCount = (int) Math.round(splitsPerDim * length / averageBoundLength);
      double splitLength = length / splitCount;
      return IntStream.range(0, splitCount)
          .mapToObj(i -> new SpatialKey.Bound(bound.min() + splitLength * i, splitLength * (i + 1)))
          .collect(Collectors.toSet());
    }

    private class BoundsCombiner {

      Set<SpatialKey> combine = Sets.newHashSet();

      private final List<Set<SpatialKey.Bound>> bounds;

      public BoundsCombiner(List<Set<SpatialKey.Bound>> bounds) {
        this.bounds = bounds;
      }

      public Set<SpatialKey> combine() {
        ImmutableMultimap.Builder<Integer, SpatialKey.Bound> builder = ImmutableMultimap.builder();
        IntStream.range(0, bounds.size())
            .forEach(i -> builder.putAll(i, bounds.get(i)));
        return combine;
      }
    }
  }
}
