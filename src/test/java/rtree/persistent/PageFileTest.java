package rtree.persistent;

import org.assertj.core.api.AbstractCharSequenceAssert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;
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
    int numOfPages = 5;
    List<PageId> ids = IntStream.range(0, numOfPages)
        .mapToObj(i -> pageFile.newPage())
        .collect(Collectors.toList());
    int numOfPointers = (int) ids.stream().mapToLong(PageId::pageIndex).distinct().count();
    assertThat(numOfPointers).isEqualTo(ids.size());
    long numOfActualPages = ids.stream()
        .map(pageFile::getById)
        .peek(page -> page.writeByHeader("testHeader1", "testContent1"))
        .peek(page -> page.writeByHeader("testHeader2", "testContent2"))
        .peek(page -> checkPageHeader(page, "testHeader1", "testContent1"))
        .peek(page -> checkPageHeader(page, "testHeader2", "testContent2"))
        .count();
    assertThat(numOfActualPages).isEqualTo(numOfPages);
    PageId idToErase = ids.get(2);
    pageFile.getById(idToErase).erase();
    int numAfterOneErased = (int) ids.stream()
        .map(pageFile::getById)
        .filter(page -> page != null)
        .count();
    assertThat(numAfterOneErased).isEqualTo(numOfPages - 1);
    assertThat(pageFile.getById(idToErase)).isNull();
  }

  private void checkPageHeader(Page page, String header, String content) {
    assertThat(page.<String>getByHeader(header)).isEqualTo(content);
  }
}
