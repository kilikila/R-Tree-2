package rtree;

public abstract class Node {
  private final HyperBox box;

  public Node(HyperBox box) {
    this.box = box;
  }

  public HyperBox hyperBox() {
    return box;
  }
}
