package rtree;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.knowm.xchart.*;
import org.knowm.xchart.internal.style.Styler;
import org.knowm.xchart.internal.style.markers.None;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SpatialKeyVisualizer {

  public void visualize(Set<SpatialKey> keys, String title) {
    Chart_XY chart = createChart(keys, title);
    keys.stream()
        .map(key -> chart.addSeries(UUID.randomUUID().toString(), getXAsList(key), getYAsList(key)))
        .peek(series -> series.setChartXYSeriesRenderStyle(Series_XY.ChartXYSeriesRenderStyle.Line))
        .forEach(series -> series.setMarker(new None()));
    new SwingWrapper<>(chart).displayChart(title);
  }

  private List<Double> getXAsList(SpatialKey key) {
    SpatialKey.Bound bound = key.bound(0);
    return Lists.newArrayList(bound.min(), bound.min(), bound.max(), bound.max(), bound.min());
  }

  private List<Double> getYAsList(SpatialKey key) {
    SpatialKey.Bound bound = key.bound(1);
    return Lists.newArrayList(bound.min(), bound.max(), bound.max(), bound.min(), bound.min());
  }

  private Chart_XY createChart(Set<SpatialKey> keys, String title) {
    Preconditions.checkArgument(keys.stream().allMatch(key -> key.dimensions() == 2), "All keys must be 2d");
    SpatialKey union = SpatialKey.union(keys);
    double coef = 1000 / (union.bound(0).length() + union.bound(1).length());
    Chart_XY chart = new ChartBuilder_XY()
        .width((int) (union.bound(0).length() * 1.1 * coef))
        .height((int) (union.bound(1).length() * 1.1 * coef))
        .title(title)
        .xAxisTitle("X")
        .yAxisTitle("Y")
        .theme(Styler.ChartTheme.XChart)
        .build();
    Styler_XY styler = chart.getStyler();
    styler.setChartTitleVisible(false);
    styler.setDefaultSeriesRenderStyle(Series_XY.ChartXYSeriesRenderStyle.Line);
    styler.setLegendVisible(false);
    return chart;
  }

}
