package rtree.persistent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class PageFile implements PageAccessor {

  private static final int PAGE_SIZE = 10000;

  private final RandomAccessFile file;

  public PageFile(String filename) {
    try {
      file = new RandomAccessFile(filename, "rw");
      initialise();
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("File " + filename + " is unreachable");
    }
  }

  private void initialise() {
    createPagesTable();
  }

  public Page getById(PageId id) {
    return new Page(new ContentAccessor(id));
  }

  public PageId newPage() {
    long pointer = getAvailablePagePointer();
    PageId pageId = new PageId(pointer);
    allocatePage(pageId);
    return pageId;
  }

  public long getAvailablePagePointer() {
    Set<Integer> pages = pagesTable().stream().sorted().collect(Collectors.toSet());
    if (pages.size() == 0) {
      return getPointerForPageIndex(0);
    }
    Iterator<Integer> iterator = pages.iterator();
    int prev = iterator.next();
    if (prev != 0) {
      return getPointerForPageIndex(0);
    }
    while (iterator.hasNext()) {
      int curr = iterator.next();
      if (curr - prev != 1) {
        break;
      }
      prev = curr;
    }
    return getPointerForPageIndex(prev + 1);
  }

  private long getPointerForPageIndex(int i) {
    return PAGE_SIZE * (i + 1);
  }

  private Set<Integer> pagesTable() {
    String content = new ContentAccessor(new PageId(0)).getContent();
    CollectionType type = TypeFactory.defaultInstance().constructCollectionType(HashSet.class, Integer.TYPE);
    try {
      return new ObjectMapper().readValue(content, type);
    } catch (IOException e) {
      throw new IllegalStateException("IOException");
    }
  }

  private void createPagesTable() {
    writePagesTable(new HashSet<>());
  }

  private void writePagesTable(HashSet<String> pagesTable) {
    try {
      String content = new ObjectMapper().writeValueAsString(pagesTable);
      new ContentAccessor(new PageId(0)).setContent(content);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("JsonProcessingException");
    }
  }

  private class ContentAccessor implements PageContentAccessor {

    private final PageId id;

    public ContentAccessor(PageId id) {
      this.id = id;
    }

    @Override
    public String getContent() {
      try {
        file.seek(id.pointer());
        byte[] buf = new byte[PAGE_SIZE];
        file.read(buf);
        return new String(buf);
      } catch (IOException e) {
        throw new IllegalStateException("IOException");
      }
    }

    @Override
    public void setContent(String content) {
      try {
        file.seek(id.pointer());
        file.write(content.getBytes());
      } catch (IOException e) {
        throw new IllegalStateException("IOException");
      }
    }

    @Override
    public void eraseContent() {
      setContent(new String(new byte[PAGE_SIZE]));
      deallocatePage(id);
    }

  }

  private void allocatePage(PageId id) {
    long pointer = id.pointer();
    try {
      file.seek(pointer);
    } catch (IOException e) {
      try {
        file.setLength(file.length() + PAGE_SIZE);
        file.seek(pointer);
      } catch (IOException e1) {
        throw new IllegalStateException("File is corrupted");
      }
    }
    try {
      byte[] bytes = new ObjectMapper().writeValueAsBytes(new HashMap<String, String>());
      file.write(bytes);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("JsonProcessingException");
    } catch (IOException e) {
      throw new IllegalStateException("IOException");
    }
  }

  private void deallocatePage(PageId id) {

  }
}
