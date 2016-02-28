package rtree.persistent;

import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class PageFileTest {

  private PageFile pageFile;

  @Before
  public void setUp() {
    pageFile = new PageFile("testpagefile.txt", 1024);
  }

  @Test
  public void testPageFile() {
    int numOfPages = 20;
    List<PageId> ids = IntStream.range(0, numOfPages)
        .mapToObj(i -> pageFile.newPage())
        .collect(Collectors.toList());
    int numOfPointers = (int) ids.stream().mapToLong(PageId::pageIndex).distinct().count();
    assertThat(numOfPointers).isEqualTo(ids.size());
    int numOfActualPages = (int) modifyAndCount(ids);
    assertThat(numOfActualPages).isEqualTo(numOfPages);

    PageId idToErase = ids.get(2);
    pageFile.getById(idToErase).erase();
    List<PageId> newIds = deleteNullIds(ids);
    int numAfterOneErased = newIds.size();
    assertThat(numAfterOneErased).isEqualTo(numOfPages - 1);
    assertThat(pageFile.getById(idToErase)).isNull();

    int numAfterModified = (int) modifyAndCount(newIds);
    assertThat(numAfterModified).isEqualTo(numAfterOneErased);

    PageId newPage = pageFile.newPage();
    assertThat(newPage.pageIndex()).isEqualTo(idToErase.pageIndex());
    ZonedDateTime now = ZonedDateTime.now();
    pageFile.getById(newPage).writeByHeader("time", now);
    ZonedDateTime time = pageFile.getById(newPage).getByHeader("time");
    assertThat(time).isEqualTo(now);
  }

  private List<PageId> deleteNullIds(List<PageId> ids) {
    return ids.stream()
          .filter(id -> pageFile.getById(id) != null)
          .collect(Collectors.toList());
  }

  private long modifyAndCount(List<PageId> ids) {
    return ids.stream()
        .map(pageFile::getById)
        .peek(page -> page.writeByHeader("testHeader1", "testContent1"))
        .peek(page -> page.writeByHeader("testHeader2", "testContent2"))
        .peek(page -> checkPageHeader(page, "testHeader1", "testContent1"))
        .peek(page -> checkPageHeader(page, "testHeader2", "testContent2"))
        .count();
  }

  private void checkPageHeader(Page page, String header, String content) {
    assertThat(page.<String>getByHeader(header)).isEqualTo(content);
  }
}
