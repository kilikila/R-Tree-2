package rtree.persistent;

import com.google.common.collect.Sets;
import rtree.Node;
import rtree.SpatialKey;
import rtree.TreeNode;

import java.util.Set;
import java.util.stream.Stream;

public class PersistentTreeNode extends PersistentNode implements TreeNode {

  static final String HEADER_SUB_NODES = "subNodes";

  public PersistentTreeNode(PageId id, SpatialKey key, PageAccessor pageAccessor) {
    super(id, key, pageAccessor);
    page().writeByHeader(HEADER_SUB_NODES, Sets.newHashSet());
  }

  @Override
  public Stream<Node> subNodes() {
    Set<PageId> ids = page().getByHeader(HEADER_SUB_NODES);
    return ids.stream().map(this::toNode);
  }

  @Override
  public int numOfSubs() {
    return page().<Set<PageId>>getByHeader(HEADER_SUB_NODES).size();
  }

  @Override
  public void addSubNode(Node subNode) {
    if (subNode instanceof PersistentNode) {
      PersistentNode persistentNode = (PersistentNode) subNode;
      page().<Set<PageId>>modifyByHeader(HEADER_SUB_NODES, subNodes -> subNodes.add(persistentNode.id));
    } else {
      throw new IllegalStateException("Sub node is not persistent");
    }
  }

  @Override
  public void removeSub(TreeNode subNode) {
    if (subNode instanceof PersistentNode) {
      PersistentNode persistentNode = (PersistentNode) subNode;
      page().<Set<Object>>modifyByHeader(HEADER_SUB_NODES, subNodes -> subNodes.remove(persistentNode.id));
      persistentNode.page().erase();
    } else {
      throw new IllegalStateException("Sub node is not persistent");
    }
  }

  private PersistentNode toNode(PageId id) {
    Page page = pageAccessor.getById(id);
    SpatialKey spatialKey = page.getByHeader(PersistentNode.HEADER_KEY);
    if (page.isHeaderPresent(PersistentTreeNode.HEADER_SUB_NODES)) {
      return new PersistentTreeNode(id, spatialKey, pageAccessor);
    } else if (page.isHeaderPresent(PersistentLeafNode.HEADER_DATA)) {
      Object data = page.getByHeader(PersistentLeafNode.HEADER_DATA);
      return new PersistentLeafNode<>(id, spatialKey, pageAccessor, data);
    }
    throw new IllegalStateException("Unknown node type");
  }
}
