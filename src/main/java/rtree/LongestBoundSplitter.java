package rtree;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LongestBoundSplitter extends MinMaxSplitter {

  public LongestBoundSplitter(int minSubNodes, int maxSubNodes) {
    super(minSubNodes, maxSubNodes);
  }

  protected Collection<Collection<Node>> divideSubNodes(TreeNode node) {
    int dimension = getLongestBoundDimension(node);
    List<Node> nodes = node.subNodes()
        .stream()
        .sorted(Comparator.comparingDouble((subNode) -> boundMin(subNode, dimension)))
        .collect(Collectors.toList());
    double nodesInDivision = 1.0 * nodes.size() / minSubNodes;
    return IntStream.range(0, minSubNodes)
        .map(i -> (int) (i * nodesInDivision))
        .mapToObj(fromIndex -> nodes.subList(fromIndex, (int) (fromIndex + nodesInDivision) + 1))
        .collect(Collectors.toSet());
  }

  private int getLongestBoundDimension(TreeNode node) {
    SpatialKey key = node.spatialKey();
    int longestDim = 0;
    double maxLength = key.bound(longestDim).length();
    for (int i = 0; i < key.dimensions(); i++) {
      if (key.bound(i).length() > maxLength){
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
