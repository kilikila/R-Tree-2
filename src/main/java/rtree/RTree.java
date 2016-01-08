package rtree;

import com.google.common.base.Preconditions;
import rtree.key.HyperBox;

import java.util.Set;

public class RTree<K extends SpatialKey, T> {

  private final int dimensions;

  private TreeNode rootNode;

  private NodeSplitter splitter;

  public RTree(int dimensions, NodeSplitter splitter) {
    this.dimensions = dimensions;
    this.splitter = splitter;
  }

  public int dimensions() {
    return dimensions;
  }

  public void insert(K key, T data) {
    checkKey(key);
  }

  public Set<T> search(K queryKey) {
    return null;
  }

  private void checkKey(K key) {
    Preconditions.checkArgument(key.dimensions() == dimensions,
        "Attempt to insert by key with unsupported number dimensions. Expected: %s, got: %s",
        dimensions, key.dimensions());
  }

  public static Builder<HyperBox, Object> builder() {
    return new Builder<>();
  }

  public static class Builder<K extends SpatialKey, T> {

    private int dimensions = 2;

    private NodeSplitter splitter;

    public RTree<K, T> create() {
      return new RTree<>(dimensions, splitter);
    }

    public Builder<K, T> dimensions(int dimensions) {
      Preconditions.checkArgument(dimensions > 0, "Dimensions must be positive, you set %s", dimensions);
      this.dimensions = dimensions;
      return this;
    }

    public <P extends SpatialKey> Builder<P, T> spatialKey(Class<P> keyClass) {
      Builder<P, T> builder = new Builder<>();
      return builder.dimensions(dimensions).nodeSplitter(splitter);
    }

    public <D> Builder<K, D> dataType(Class<D> dataClass) {
      Builder<K, D> builder = new Builder<>();
      return builder.dimensions(dimensions).nodeSplitter(splitter);
    }

    public Builder<K, T> nodeSplitter(NodeSplitter splitter) {
      this.splitter = splitter;
      return this;
    }
  }
}