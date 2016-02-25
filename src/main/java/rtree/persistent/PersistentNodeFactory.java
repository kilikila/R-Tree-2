package rtree.persistent;

import rtree.LeafNode;
import rtree.NodeFactory;
import rtree.SpatialKey;
import rtree.TreeNode;

public class PersistentNodeFactory implements NodeFactory {

  private final PageFile pageFile;

  public PersistentNodeFactory(String filename) {
    this.pageFile = new PageFile(filename);
  }

  @Override
  public TreeNode treeNode(SpatialKey key) {
    return new PersistentTreeNode(pageFile.newPage(), key, pageFile);
  }

  @Override
  public <T> LeafNode<T> leaf(SpatialKey key, T data) {
    return new PersistentLeafNode<>(pageFile.newPage(), key, pageFile, data);
  }
}
