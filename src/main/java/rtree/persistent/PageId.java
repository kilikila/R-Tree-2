package rtree.persistent;

public class PageId {
  private final long pointer;

  public PageId(long pointer) {
    this.pointer = pointer;
  }

  public long pointer() {
    return pointer;
  }
}
