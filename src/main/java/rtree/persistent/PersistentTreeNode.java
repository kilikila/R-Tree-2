package rtree.persistent;

import com.google.common.collect.Sets;
import rtree.Node;
import rtree.SpatialKey;
import rtree.TreeNode;

import java.util.Set;
import java.util.stream.Stream;

public class PersistentTreeNode extends PersistentNode implements TreeNode {

  private final NodeRetriever nodeRetriever;

  public PersistentTreeNode(Page page, SpatialKey key, NodeRetriever nodeRetriever) {
    super(page, key);
    this.nodeRetriever = nodeRetriever;
    page.writeByHeader("subNodes", Sets.newHashSet());
  }

  @Override
  public Stream<Node> subNodes() {
    Set<Object> ids = page.getByHeader("subNodes");
    return ids.stream().map(nodeRetriever::getPageById);
  }

  @Override
  public int numOfSubs() {
    return page.getByHeader("numOfSubs");
  }

  @Override
  public void addSubNode(Node subNode) {
    if (subNode instanceof PersistentNode) {
      Object id = ((PersistentNode) subNode).page.getId();
      page.<Set<Object>>modifyByHeader("subNodes", sn -> sn.add(id));
    } else {
      throw new IllegalStateException("Sub node is not persistent");
    }
  }

  @Override
  public void removeSub(TreeNode subNode) {
    if (subNode instanceof PersistentNode) {
      Object id = ((PersistentNode) subNode).page.getId();
      page.<Set<Object>>modifyByHeader("subNodes", sn -> sn.remove(id));
    } else {
      throw new IllegalStateException("Sub node is not persistent");
    }
  }
}
