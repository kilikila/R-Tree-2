package rtree;

import java.util.Set;

public interface NodeSplitter {

  Set<Node> split(Node node);
}
