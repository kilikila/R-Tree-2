package rtree;

import java.util.Optional;
import java.util.Set;

@FunctionalInterface
public interface NodeSplitter {

  Optional<Set<SpatialKey>> split(TreeNode node);
}
