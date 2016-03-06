package rtree;

import rtree.factories.DividerFactory;
import rtree.factories.NodeComparatorFactory;
import rtree.factories.NodeFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class VisualizableRTree<T> extends RTree<T> {

  public VisualizableRTree(RTree tree) {
    this(tree.dimensions, tree.minSubNodes, tree.maxSubNodes, tree.nodeFactory, tree.dividerFactory, tree.nodeComparatorFactory);
    this.rootNode = tree.rootNode;
  }

  public VisualizableRTree(int dimensions, int minSubNodes, int maxSubNodes, NodeFactory nodeFactory, DividerFactory dividerFactory, NodeComparatorFactory nodeComparatorFactory) {
    super(dimensions, minSubNodes, maxSubNodes, nodeFactory, dividerFactory, nodeComparatorFactory);
  }

  public void visualize() {
    new SpatialKeyVisualizer().visualize(getSubNodesKeys(), "Root sub nodes");
  }

  private Set<SpatialKey> getSubNodesKeys() {
    return rootNode.subNodes().map(Node::spatialKey).collect(Collectors.toSet());
  }
}
