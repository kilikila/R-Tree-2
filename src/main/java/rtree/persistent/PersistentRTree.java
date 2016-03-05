package rtree.persistent;

import rtree.RTree;
import rtree.factories.DivisionPerformerFactory;
import rtree.factories.NodeComparatorFactory;

public class PersistentRTree<T> extends RTree<T>{

  public PersistentRTree(int dimensions,
                         int minSubNodes,
                         int maxSubNodes,
                         PageFile pageFile,
                         DivisionPerformerFactory divisionPerformerFactory,
                         NodeComparatorFactory nodeComparatorFactory) {
    super(dimensions, minSubNodes, maxSubNodes,
        new PersistentNodeFactory(pageFile), divisionPerformerFactory, nodeComparatorFactory);
  }

  public static <V> PersistentBuilder<V> builder(PageFile pageFile) {
    return new PersistentBuilder<>(pageFile);
  }

  public static class PersistentBuilder<T> extends RTree.Builder<T> {

    private final PageFile pageFile;

    public PersistentBuilder(PageFile pageFile) {
      this.pageFile = pageFile;
    }

    @Override
    public PersistentRTree<T> create() {
      return new PersistentRTree<>(dimensions, minSubNodes, maxSubNodes, pageFile, divisionPerformerFactory, nodeComparatorFactory);
    }
  }
}
