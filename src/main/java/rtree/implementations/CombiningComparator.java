package rtree.implementations;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import rtree.SpatialKey;
import rtree.factories.KeyComparatorFactory;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class CombiningComparator implements Comparator<SpatialKey> {

  private final Set<Comparator<SpatialKey>> comparators;

  public CombiningComparator(Set<Comparator<SpatialKey>> comparators) {
    this.comparators = comparators;
  }

  @Override
  public int compare(SpatialKey o1, SpatialKey o2) {
    return ((int) comparators.stream()
        .mapToInt(c -> c.compare(o1, o2))
        .average().getAsDouble());
  }

  public static KeyComparatorFactory factory(KeyComparatorFactory... factories) {
    Preconditions.checkArgument(factories.length > 0);
    return new CombiningComparatorFactory(Sets.newHashSet(factories));
  }

  private static class CombiningComparatorFactory implements KeyComparatorFactory {

    private final Set<KeyComparatorFactory> factories;

    public CombiningComparatorFactory(Set<KeyComparatorFactory> factories) {
      this.factories = factories;
    }

    @Override
    public Comparator<SpatialKey> supplyComparator(SpatialKey keyToInsert) {
      Set<Comparator<SpatialKey>> comparators = Sets.newHashSet(factories).stream()
          .map(factory -> factory.supplyComparator(keyToInsert))
          .collect(Collectors.toSet());
      return new CombiningComparator(comparators);
    }
  }
}
