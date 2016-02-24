package rtree.persistent;

public class PageFile {

  public PageFile(String filename) {

  }

  public Page getById(Object id) {
    return null;
  }

  public Page newPage() {
    return new Page(this);
  }

  public static PageFile parse(String filename) {
    return null;
  }
}
