package rtree.persistent;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class PageFileTest {

  private PageFile pageFile;

  @Before
  public void setUp() {
    pageFile = new PageFile("testpagefile.txt");
  }

  @Test
  public void testGetById() {
    Set<PageId> ids = IntStream.range(0, 5)
        .mapToObj(i -> pageFile.newPage()).collect(Collectors.toSet());
    int numOfPointers = (int) ids.stream().mapToLong(PageId::pointer).distinct().count();
    assertThat(numOfPointers).isEqualTo(ids.size());
    boolean headerPresent = ids.stream()
        .map(pageFile::getById)
        .peek(page -> page.writeByHeader("testHeader", "testContent"))
        .allMatch(page -> page.isHeaderPresent("testHeader"));
    assertThat(headerPresent).isTrue();
  }
}
