package rtree.implementations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import rtree.KeyDivider;
import rtree.SpatialKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class EachBoundKeyDivider implements KeyDivider {

  protected final Set<SpatialKey> subKeys;

  public EachBoundKeyDivider(Set<SpatialKey> subKeys) {
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

  protected abstract List<Set<SpatialKey.Bound>> getNewBoundSets(int divisionCount);

  private class BoundsCombiner {

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
