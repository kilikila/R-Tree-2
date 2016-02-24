package rtree.persistent;

import com.google.common.collect.Sets;
import rtree.Node;
import rtree.SpatialKey;
import rtree.TreeNode;

import java.util.Set;
import java.util.stream.Stream;

public class PersistentTreeNode extends PersistentNode implements TreeNode {

  static final String HEADER_SUB_NODES = "subNodes";

  static final String headerNumOfSubNodes = "numOfSubs";

  private final NodeRetriever nodeRetriever;

  public PersistentTreeNode(Page page, SpatialKey key, NodeRetriever nodeRetriever) {
    super(page, key);
    this.nodeRetriever = nodeRetriever;
    page.writeByHeader(HEADER_SUB_NODES, Sets.newHashSet());
  }

  @Override
  public Stream<Node> subNodes() {
    Set<Object> ids = page.getByHeader(HEADER_SUB_NODES);
    return ids.stream().map(nodeRetriever::getByPageId);
  }

  @Override
  public int numOfSubs() {
    return page.getByHeader(headerNumOfSubNodes);
  }

  @Override
  public void addSubNode(Node subNode) {
    if (subNode instanceof PersistentNode) {
      Object id = ((PersistentNode) subNode).page.getId();
      page.<Set<Object>>modifyByHeader(HEADER_SUB_NODES, subNodes -> subNodes.add(id));
    } else {
      throw new IllegalStateException("Sub node is not persistent");
    }
  }

  @Override
  public void removeSub(TreeNode subNode) {
    if (subNode instanceof PersistentNode) {
      PersistentNode persistentNode = (PersistentNode) subNode;
      Object id = persistentNode.page.getId();
      page.<Set<Object>>modifyByHeader(HEADER_SUB_NODES, subNodes -> subNodes.remove(id));
      persistentNode.page.erase();
    } else {
      throw new IllegalStateException("Sub node is not persistent");
    }
  }
}
