package rtree.persistent;

public class PageId {
  private final int pageIndex;

  public PageId(int pageIndex) {
    this.pageIndex = pageIndex;
  }

  public int pageIndex() {
    return pageIndex;
  }
}
