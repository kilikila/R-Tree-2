package rtree;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.knowm.xchart.*;
import org.knowm.xchart.internal.style.Styler;
import org.knowm.xchart.internal.style.markers.None;

import java.util.List;
import java.util.Set;

public class SpatialKeyVisualizer {

  public void visualize(Set<SpatialKey> keys, String title) {
    Chart_XY chart = createChart(keys, title);
    keys.stream()
        .map(key -> chart.addSeries("k " + key.volume(), getXAsList(key), getYAsList(key)))
        .peek(series -> series.setChartXYSeriesRenderStyle(Series_XY.ChartXYSeriesRenderStyle.Line))
        .forEach(series -> series.setMarker(new None()));
    new SwingWrapper<>(chart).displayChart();
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
    double coef = 500 / (union.bound(0).length() + union.bound(1).length());
    Chart_XY chart = new ChartBuilder_XY()
        .width((int) (union.bound(0).length() * 1.1 * coef) + 200)
        .height((int) (union.bound(1).length() * 1.1 * coef))
        .title(title)
        .xAxisTitle("X")
        .yAxisTitle("Y")
        .theme(Styler.ChartTheme.XChart)
        .build();
    Styler_XY styler = chart.getStyler();
    styler.setDefaultSeriesRenderStyle(Series_XY.ChartXYSeriesRenderStyle.Scatter);
    styler.setChartTitleVisible(false);
    styler.setLegendVisible(false);
    styler.setMarkerSize(16);
    return chart;
  }

}
