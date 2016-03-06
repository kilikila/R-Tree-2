package rtree;

import java.util.Set;

public interface Divider {

  Set<SpatialKey> divide(int numOfSubNodes);
}
