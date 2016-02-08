package rtree;

public abstract class MinimalMetricsSelector implements SubNodeSelector {

  @Override
  public TreeNode chooseSubNode(TreeNode node, LeafNode<?> leafNode) {
    return new NodeSelectorPerformer(node, leafNode).choose();
  }

  class NodeSelectorPerformer {

    private TreeNode node;

    private NodeComparator nodeComparator;

    public NodeSelectorPerformer(TreeNode node, LeafNode<?> leafNode) {
      this.node = node;
      this.nodeComparator = supplyComparator(leafNode);
    }

    public TreeNode choose() {
      Node chosenSubNode = node.subNodes()
          .stream()
          .min(nodeComparator)
          .orElseThrow(IllegalStateException::new);
      if (chosenSubNode instanceof TreeNode) {
        return (TreeNode) chosenSubNode;
      } else {
        throw new IllegalStateException("Incorrect node type");
      }
    }
  }

  protected abstract NodeComparator supplyComparator(LeafNode<?> leafNode);
}
