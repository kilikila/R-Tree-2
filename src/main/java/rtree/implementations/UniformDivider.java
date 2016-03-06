package rtree.implementations;

import rtree.SpatialKey;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UniformDivider extends CombiningDivider {

  private double penaltySplits = 0;

  public UniformDivider(Set<SpatialKey> subKeys) {
    super(subKeys);
  }

  @Override
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
      if (penaltySplits < 0) {
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

}
