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
  
}
