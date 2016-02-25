package rtree.persistent;

public interface PageContentAccessor {

  String getContent();

  void setContent(String content);

  void eraseContent();
}
