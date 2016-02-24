package rtree.persistent;

import rtree.LeafNode;
import rtree.NodeFactory;
import rtree.SpatialKey;
import rtree.TreeNode;

public class PersistentNodeFactory implements NodeFactory {

  private final NodeRetriever retriever;

  private final PageFile pageFile;

  public PersistentNodeFactory(String filename) {
    this.pageFile = new PageFile(filename);
    retriever = new NodeRetriever(pageFile);
  }

  private PersistentNodeFactory(PageFile pageFile) {
    this.pageFile = pageFile;
    retriever = new NodeRetriever(pageFile);
  }

  @Override
  public TreeNode treeNode(SpatialKey key) {
    return new PersistentTreeNode(pageFile.newPage(), key, retriever);
  }

  @Override
  public <T> LeafNode<T> leaf(SpatialKey key, T data) {
    return new PersistentLeafNode<>(pageFile.newPage(), key, data);
  }

  public static PersistentNodeFactory fromFile(String filename) {
    PageFile pageFile = PageFile.parse(filename);
    return new PersistentNodeFactory(pageFile);
  }
}
