package rtree;

public class SubNodeSelector {

  private final NodeComparator nodeComparator;

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
          .min(nodeComparator)
          .orElseThrow(IllegalStateException::new);
      if (chosenSubNode instanceof TreeNode.InMemory) {
        return (TreeNode) chosenSubNode;
      } else {
        throw new IllegalStateException("Incorrect node type");
      }
    }
  }
}
