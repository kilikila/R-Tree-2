package rtree;

import java.util.Set;

@FunctionalInterface
public interface NodeSplitter {

  Set<Node> split(Node node);
}
