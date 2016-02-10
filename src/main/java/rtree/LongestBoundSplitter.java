package rtree;

import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LongestBoundSplitter extends OverflowSplitter {

  private final Set<TreeNode> divisions = Sets.newHashSet();

  public LongestBoundSplitter(int minSubNodes, int maxSubNodes) {
    super(minSubNodes, maxSubNodes);
  }

  public Set<TreeNode> divide(TreeNode node) {
    divisions.clear();
    divisions.add(node);
    while (divisions.size() < minSubNodes) {
      splitBiggest();
    }
    return divisions;
  }

  private void splitBiggest() {
    TreeNode division = biggestDivision();
    int dimension = getLongestBoundDimension(division.spatialKey());
    List<Node> sortedNodes = division.subNodes().stream()
        .sorted(Comparator.comparingDouble((subNode) -> boundMin(subNode, dimension)))
        .collect(Collectors.toList());
    int splitIndex = sortedNodes.size() / 2;
    Set<Node> nodes1 = Sets.newHashSet(sortedNodes.subList(0, splitIndex));
    Set<Node> nodes2 = Sets.newHashSet(sortedNodes.subList(splitIndex, sortedNodes.size()));
    divisions.remove(division);
    addDivision(nodes1);
    addDivision(nodes2);
  }

  private void addDivision(Set<Node> nodes) {
    SpatialKey spatialKey = nodes.iterator().next().spatialKey();
    TreeNode treeNode = new TreeNode(spatialKey);
    nodes.forEach(treeNode::addSubNode);
    divisions.add(treeNode);
  }

  private TreeNode biggestDivision() {
    return divisions.stream()
        .filter(node -> node.subNodes().size() >= 2)
        .max(Comparator.comparingDouble(node -> node.spatialKey().volume()))
        .get();
  }

  private int getLongestBoundDimension(SpatialKey key) {
    int longestDim = 0;
    double maxLength = key.bound(longestDim).length();
    for (int i = 0; i < key.dimensions(); i++) {
      if (key.bound(i).length() > maxLength) {
        longestDim = i;
        maxLength = key.bound(longestDim).length();
      }
    }
    return longestDim;
  }

  private double boundMin(Node node, int dimension) {
    return node.spatialKey().bound(dimension).min();
  }
}
