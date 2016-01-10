package rtree;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LongestBoundSplitter extends MinMaxSplitter {

  public LongestBoundSplitter(int minSubNodes, int maxSubNodes) {
    super(minSubNodes, maxSubNodes);
  }

  protected Set<Set<Node>> divideSubNodes(TreeNode node) {
    List<Node> nodes = node.subNodes()
        .stream()
        .sorted(Comparator.comparingDouble(this::boundMin))
        .collect(Collectors.toList());
    int halfSize = nodes.size() / 2;
    Set<Set<Node>> division = new HashSet<>(2);
    Set<Node> nodes1 = nodes.subList(0, halfSize)
        .stream()
        .collect(Collectors.toSet());
    Set<Node> nodes2 = nodes.subList(halfSize, nodes.size())
        .stream()
        .collect(Collectors.toSet());
    division.add(nodes1);
    division.add(nodes2);
    return division;
  }

  private double boundMin(Node node) {
    return node.spatialKey().bound(0).min();
  }
}
