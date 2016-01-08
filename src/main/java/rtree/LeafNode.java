package rtree;

public class LeafNode<T> extends Node {

  private final T data;

  public LeafNode(SpatialKey key, T data) {
    super(key);
    this.data = data;
  }

  public T data() {
    return data;
  }
}
