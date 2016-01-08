package rtree;

public class LeafNode<T> extends Node {

  private final T data;

  public LeafNode(HyperBox box, T data) {
    super(box);
    this.data = data;
  }

  public T data() {
    return data;
  }
}
