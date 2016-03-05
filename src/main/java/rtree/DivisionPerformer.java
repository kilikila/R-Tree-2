package rtree;

import java.util.Set;

public interface DivisionPerformer {

  Set<SpatialKey> divide(double numOfSubNodes);
}
