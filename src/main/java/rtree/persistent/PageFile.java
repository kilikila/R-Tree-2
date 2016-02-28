package rtree.persistent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class PageFile implements PageAccessor {

  private final int pageSize;

  private final RandomAccessFile file;

  private final PageId tableId = new PageId(-1);

  public PageFile(String filename, int pageSize) {
    try {
      file = new RandomAccessFile(filename, "rw");
      initialise();
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("File " + filename + " is unreachable");
    }
    this.pageSize = pageSize;
  }

  private void initialise() {
    createIndexTable();
  }

  public Page getById(PageId id) {
    if (indexTable().contains(id.pageIndex())) {
      return new Page(new PageContentAccessor(id));
    } else {
      return null;
    }
  }

  public PageId newPage() {
    int pageIndex = getAvailablePageIndex();
    PageId pageId = new PageId(pageIndex);
    allocatePage(pageId);
    return pageId;
  }

  private int getAvailablePageIndex() {
    Set<Integer> pages = indexTable().stream().sorted().collect(Collectors.toSet());
    if (pages.size() == 0) {
      return 0;
    }
    Iterator<Integer> iterator = pages.iterator();
    int prev = iterator.next();
    if (prev != 0) {
      return 0;
    }
    while (iterator.hasNext()) {
      int curr = iterator.next();
      if (curr - prev != 1) {
        break;
      }
      prev = curr;
    }
    return prev + 1;
  }

  private Set<Integer> indexTable() {
    String content = getPageTableAccessor().getContent();
    CollectionType type = TypeFactory.defaultInstance().constructCollectionType(HashSet.class, Integer.TYPE);
    try {
      return new ObjectMapper().readValue(content, type);
    } catch (IOException e) {
      throw new IllegalStateException("IOException");
    }
  }

  private void createIndexTable() {
    writeIndexTable(new HashSet<>());
  }

  private void writeIndexTable(Set<Integer> indexTable) {
    try {
      String content = new ObjectMapper().writeValueAsString(indexTable);
      getPageTableAccessor().setContent(content);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("JsonProcessingException");
    }
  }

  private PageContentAccessor getPageTableAccessor() {
    return new PageContentAccessor(tableId);
  }

  class PageContentAccessor {

    private final PageId id;

    public PageContentAccessor(PageId id) {
      this.id = id;
    }

    public String getContent() {
      try {
        file.seek(position(id));
        byte[] buf = new byte[pageSize];
        file.read(buf);
        return new String(buf);
      } catch (IOException e) {
        throw new IllegalStateException("IOException");
      }
    }

    public void setContent(String content) {
      try {
        file.seek(position(id));
        file.write(content.getBytes());
      } catch (IOException e) {
        throw new IllegalStateException("IOException");
      }
    }

    public void eraseContent() {
      setContent(new String(new byte[pageSize]));
      deallocatePage(id);
    }

  }

  private void allocatePage(PageId id) {
    long pointer = position(id);
    setFilePointer(pointer);
    new PageContentAccessor(id).eraseContent();
    Set<Integer> indexTable = indexTable();
    indexTable.add(id.pageIndex());
    writeIndexTable(indexTable);
  }

  private void setFilePointer(long pointer) {
    try {
      file.seek(pointer);
    } catch (IOException e) {
      try {
        file.setLength(file.length() + pageSize);
        file.seek(pointer);
      } catch (IOException e1) {
        throw new IllegalStateException("File is corrupted");
      }
    }
  }

  private void deallocatePage(PageId id) {
    Set<Integer> indexTable = indexTable();
    indexTable.remove(id.pageIndex());
    writeIndexTable(indexTable);
  }

  private long position(PageId id) {
    return (id.pageIndex() + 1) * pageSize;
  }
}
