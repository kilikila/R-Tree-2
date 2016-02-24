package rtree.persistent;

import java.util.function.Consumer;

public class Page {

  private final PageFile pageFile;

  public Page(PageFile pageFile) {
    this.pageFile = pageFile;
  }

  public <T> T getByHeader(String header) {
    return (T) header;
  }

  public <T> void modifyByHeader(String header, Consumer<T> objectModifier) {
    T object = getByHeader(header);
    objectModifier.accept(object);
    writeByHeader(header, object);
  }

  public void writeByHeader(String header, Object obj) {

  }

  public Object getId() {
    return null;
  }

  public boolean isHeaderPresent(String header) {
    return false;
  }

  public void erase() {

  }
}
