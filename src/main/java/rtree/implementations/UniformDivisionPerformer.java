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

  protected final Set<SpatialKey> subKeys;

  private double penaltySplits = 0;

  public UniformDivisionPerformer(Set<SpatialKey> subKeys) {
    this.subKeys = subKeys;
  }

  public Set<SpatialKey> divide(int divisionCount) {
    List<Set<SpatialKey.Bound>> newBounds = getNewBoundSets(divisionCount);
    throwIfNoSplit(newBounds);
    return new BoundsCombiner(newBounds).combine();
  }

  private void throwIfNoSplit(List<Set<SpatialKey.Bound>> newBounds) {
    if (newBounds.stream().allMatch(bounds -> bounds.size() < 2)) {
      throw new IllegalStateException("No split performed");
    }
  }

  protected List<Set<SpatialKey.Bound>> getNewBoundSets(int divisionCount) {
    SpatialKey boundingKey = SpatialKey.union(subKeys);
    double splitsPerDim = Math.pow(divisionCount, 1.0 / boundingKey.dimensions());
    double averageBoundLength = boundingKey.bounds()
        .mapToDouble(SpatialKey.Bound::length)
        .average().getAsDouble();
    return boundingKey.bounds()
        .map(bound -> splitBound(bound, getSplitCount(bound, splitsPerDim, averageBoundLength)))
        .collect(Collectors.toList());
  }

  private int getSplitCount(SpatialKey.Bound bound, double splitsPerDim, double averageBoundLength) {
    double actualSplits = splitsPerDim * bound.length() / averageBoundLength;
    int splitCount;
    if (isInteger(actualSplits) || isInteger(splitsPerDim)) {
      splitCount = (int) Math.round(actualSplits);
    } else {
      if (penaltySplits < 1) {
        penaltySplits += actualSplits - (int) actualSplits;
        splitCount = (int) actualSplits + 1;
      } else {
        penaltySplits -= actualSplits - (int) actualSplits;
        splitCount = (int) actualSplits;
      }
    }
    return splitCount > 1 ? splitCount : 1;
  }

  private boolean isInteger(double val) {
    return val == Math.round(val);
  }

  private Set<SpatialKey.Bound> splitBound(SpatialKey.Bound bound, int splitCount) {
    double splitLength = bound.length() / splitCount;
    return IntStream.range(0, splitCount)
        .mapToObj(i -> new SpatialKey.Bound(bound.min() + splitLength * i, bound.min() + splitLength * (i + 1)))
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
      if (combine.size() == 0) {
        throw new IllegalStateException("Empty split");
      }
      return combine;
    }

    private void recursivelyAdd(List<SpatialKey.Bound> bounds) {
      if (bounds.size() == boundsByDim.size()) {
        combine.add(new SpatialKey(bounds));
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
