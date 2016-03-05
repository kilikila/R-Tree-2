package rtree.implementations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import rtree.DivisionPerformer;
import rtree.SpatialKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UniformDivisionPerformer implements DivisionPerformer {

  private final SpatialKey boundingKey;

  public UniformDivisionPerformer(Set<SpatialKey> subKeys) {
    boundingKey = SpatialKey.union(subKeys);
  }

  public Set<SpatialKey> divide(double divisionCount) {
    double splitsPerDim = Math.pow(divisionCount, boundingKey.dimensions());
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

  protected class BoundsCombiner {

    Set<SpatialKey> combine = Sets.newHashSet();

    private final List<Set<SpatialKey.Bound>> boundsByDim;

    public BoundsCombiner(List<Set<SpatialKey.Bound>> boundsByDim) {
      this.boundsByDim = boundsByDim;
    }

    public Set<SpatialKey> combine() {
      recursivelyAdd(new ArrayList<>());
      return combine;
    }

    private void recursivelyAdd(List<SpatialKey.Bound> bounds) {
      if (bounds.size() == boundsByDim.size()) {
        combine.add(new SpatialKey(bounds));
        return;
      } else {
        boundsByDim.get(bounds.size()).forEach(bound -> addAndProceed(bounds, bound));
      }
    }

    private void addAndProceed(List<SpatialKey.Bound> bounds, SpatialKey.Bound bound) {
      List<SpatialKey.Bound> copy = Lists.newArrayList(bounds);
      copy.add(bound);
      recursivelyAdd(copy);
    }
  }
}
