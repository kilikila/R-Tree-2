package rtree.persistent;

public class NodeRetriever {

  private final PageFile pageFile;

  public NodeRetriever(PageFile pageFile) {
    this.pageFile = pageFile;
  }

  public PersistentNode getByPageId(Object id) {
    Page page = pageFile.getById(id);
    return node(page);
  }

  private PersistentNode node(Page page) {
    if (page.isHeaderPresent(PersistentTreeNode.HEADER_SUB_NODES)) {
      return new PersistentTreeNode(page, page.getByHeader(PersistentNode.HEADER_KEY), this);
    } else if (page.isHeaderPresent(PersistentLeafNode.HEADER_DATA)) {
      return new PersistentLeafNode<>(page, page.getByHeader(PersistentNode.HEADER_KEY), page.getByHeader(PersistentLeafNode.HEADER_DATA));
    }
    throw new IllegalStateException("Unknown node type");
  }
}
