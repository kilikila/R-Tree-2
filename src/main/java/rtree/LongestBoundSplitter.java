package rtree;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LongestBoundSplitter extends OverflowSplitter {

  public LongestBoundSplitter(int minSubNodes, int maxSubNodes) {
    super(minSubNodes, maxSubNodes);
  }

  protected Set<Collection<Node>> divideSubNodes(TreeNode node) {
    int dimension = getLongestBoundDimension(node);
    List<Node> nodes = node.subNodes()
        .stream()
        .sorted(Comparator.comparingDouble((subNode) -> boundMin(subNode, dimension)))
        .collect(Collectors.toList());
    return IntStream.range(0, minSubNodes)
        .mapToObj(i -> subList(nodes, i))
        .collect(Collectors.toSet());
  }

  private List<Node> subList(List<Node> nodes, int i) {
    int fromIndex = nodes.size() * i / minSubNodes;
    int toIndex = nodes.size() * (i + 1) / minSubNodes;
    return nodes.subList(fromIndex, toIndex);
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
