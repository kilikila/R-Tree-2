package rtree;

import java.util.List;

public class HyperBox {

  private final List<BoxBound> bounds;

  public HyperBox(final List<BoxBound> bounds) {
    this.bounds = bounds;
  }

  public int dimensions() {
    return bounds.size();
  }

  public BoxBound bound(int dim) {
    return bounds.get(dim);
  }

}
