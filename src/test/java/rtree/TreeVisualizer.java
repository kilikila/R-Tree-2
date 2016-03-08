package rtree;

import com.google.common.base.Preconditions;

import java.util.Set;
import java.util.stream.Collectors;

public class TreeVisualizer {

  private final int maxNodesDisplayed;

  private final boolean showLeaves;

  private final TreeNode rootNode;

  private int nodesDisplayed = 0;

  public TreeVisualizer(TreeNode rootNode, int maxNodesDisplayed, boolean showLeaves) {
    Preconditions.checkArgument(maxNodesDisplayed >= 0);
    this.maxNodesDisplayed = maxNodesDisplayed;
    this.showLeaves = showLeaves;
    this.rootNode = rootNode;
  }

  public void visualize() {
    visualize(rootNode, 1);
    nodesDisplayed = 0;
  }

  private void visualize(Node node, int i) {
    if (node instanceof  TreeNode && nodesDisplayed < maxNodesDisplayed) {
      TreeNode treeNode = (TreeNode) node;
      boolean subsAreLeaves = subsAreLeaves(treeNode);
      if (subsAreLeaves && !showLeaves) {
        return;
      }
      nodesDisplayed++;
      new SpatialKeyVisualizer().visualize(getSubNodesKeys(treeNode), nameSubs(i, subsAreLeaves));
      treeNode.subNodes().forEach(sub -> visualize(sub, i + 1));
    }
  }

  private String nameSubs(int i, boolean subsAreLeaves) {
    return subsAreLeaves ? "Leaves" : "Level " + i + " nodes";
  }

  private boolean subsAreLeaves(TreeNode treeNode) {
    return treeNode.subNodes().iterator().next() instanceof LeafNode;
  }

  private Set<SpatialKey> getSubNodesKeys(TreeNode node) {
    return node.subNodes().map(Node::spatialKey).collect(Collectors.toSet());
  }
}
