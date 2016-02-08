package rtree;

public interface SubNodeSelector {

  TreeNode chooseSubNode(TreeNode node, LeafNode<?> leafNode);
}
