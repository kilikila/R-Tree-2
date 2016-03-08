package rtree;

import java.util.Set;

public interface KeyDivider {

  Set<SpatialKey> divide(int numOfSubNodes);
}
