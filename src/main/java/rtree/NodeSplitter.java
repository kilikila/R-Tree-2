package rtree;

import java.util.Optional;
import java.util.Set;

public interface NodeSplitter {

  Optional<Set<SpatialKey>> split(TreeNode node);
}
