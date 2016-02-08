package rtree;

import java.util.Comparator;

public interface SubNodeSelector {

  default TreeNode chooseSubNode(TreeNode node, LeafNode<?> leafNode) {
    return new NodeSelectorPerformer(node, leafNode).choose();
  }

  class NodeSelectorPerformer {

    private TreeNode node;

    private LeafNode<?> leafNode;

    public NodeSelectorPerformer(TreeNode node, LeafNode<?> leafNode) {
      this.node = node;
      this.leafNode = leafNode;
    }

    public TreeNode choose() {
      Node chosenSubNode = node.subNodes()
          .stream()
          .min(Comparator.comparingDouble(this::volumeIncrease))
          .orElseThrow(IllegalStateException::new);
      if (chosenSubNode instanceof TreeNode) {
        return (TreeNode) chosenSubNode;
      } else {
        throw new IllegalStateException("Incorrect node type");
      }
    }

    private double volumeIncrease(Node node) {
      double volume = node.spatialKey().volume();
      double volumeWithLeaf = node.spatialKey().union(leafNode.spatialKey()).volume();
      return volumeWithLeaf - volume;
    }
  }
}
