package rtree;

public interface LeafNode<T> extends Node {

  T data();

  class InMemory<T> extends Node.InMemory implements LeafNode<T> {

    private final T data;

    public InMemory(SpatialKey key, T data) {
      super(key);
      this.data = data;
    }

    @Override
    public T data() {
      return data;
    }
  }
}
