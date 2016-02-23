package rtree;

public class SubNodeSelector {

  public void setNodeComparator(NodeComparator nodeComparator) {
    this.nodeComparator = nodeComparator;
  }

  private NodeComparator nodeComparator;

  protected SubNodeSelector(NodeComparator nodeComparator) {
    this.nodeComparator = nodeComparator;
  }

  public TreeNode chooseSubNode(TreeNode node, Node nodeToInsert) {
    nodeComparator.setNodeToInsert(nodeToInsert);
    return new NodeSelectorPerformer(node).choose();
  }

  class NodeSelectorPerformer {

    private TreeNode node;

    public NodeSelectorPerformer(TreeNode node) {
      this.node = node;
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
}
