package rtree.persistent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Page {

  private final PageFile.PageContentAccessor accessor;

  public Page(PageFile.PageContentAccessor accessor) {
    this.accessor = accessor;
  }

  public <T> T getByHeader(String header) {
    String content = accessor.getContent();
    return (T) new ContentParser(content).getHeaderContent(header);
  }

  public <T> void modifyByHeader(String header, Consumer<T> objectModifier) {
    T object = getByHeader(header);
    objectModifier.accept(object);
    writeByHeader(header, object);
  }

  public void writeByHeader(String header, Object obj) {
    String content = accessor.getContent();
    ContentParser contentParser = new ContentParser(content);
    contentParser.setHeaderContent(header, obj);
    String contentString = contentParser.getContentString();
    accessor.setContent(contentString);
  }

  public boolean isHeaderPresent(String header) {
    String content = accessor.getContent();
    return new ContentParser(content).isHeaderPresent(header);
  }

  public void erase() {
    accessor.eraseContent();
  }

  private class ContentParser {

    private final ObjectMapper mapper = new ObjectMapper();

    private Map<String, String> headers;

    public ContentParser(String content) {
      MapType headerMapType = TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, String.class);
      try {
        headers = mapper.readValue(content, headerMapType);
      } catch (IOException e) {
        headers = new HashMap<>();
      }
    }

    public Object getHeaderContent(String header) {
      String headerContent = headers.get(header);
      if (headerContent == null) {
        throw new IllegalStateException("Header not found " + header);
      }
      try {
        return new ObjectInputStream(new ByteArrayInputStream(headerContent.getBytes())).readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new IllegalStateException("IO error occurred");
      }
    }

    public void setHeaderContent(String header, Object obj) {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try {
        new ObjectOutputStream(outputStream).writeObject(obj);
      } catch (IOException e) {
        throw new IllegalStateException("Failed to serialize new header content.  Header: " + header);
      }
      headers.put(header, outputStream.toString());
    }

    public boolean isHeaderPresent(String header) {
      return headers.containsKey(header);
    }

    public String getContentString() {
      try {
        return mapper.writeValueAsString(headers);
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Failed to serialize headers");
      }
    }
  }
}
