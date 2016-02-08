package rtree;

public interface SubNodeSelector {

  TreeNode chooseSubNode(TreeNode node, Node nodeToInsert);
}
