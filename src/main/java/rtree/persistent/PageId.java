package rtree.persistent;

import java.io.Serializable;

public class PageId implements Serializable{

  private final int pageIndex;

  public PageId(int pageIndex) {
    this.pageIndex = pageIndex;
  }

  public int pageIndex() {
    return pageIndex;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PageId pageId = (PageId) o;

    return pageIndex == pageId.pageIndex;

  }

  @Override
  public int hashCode() {
    return pageIndex;
  }
}
