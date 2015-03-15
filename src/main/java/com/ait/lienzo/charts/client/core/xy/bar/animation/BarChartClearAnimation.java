
package com.ait.lienzo.charts.client.core.xy.bar.animation;

import java.util.List;
import java.util.Map;

import com.ait.lienzo.charts.client.core.animation.AbstractChartAnimation;
import com.ait.lienzo.charts.client.core.legend.ChartLegend;
import com.ait.lienzo.charts.client.core.xy.XYChartSeries;
import com.ait.lienzo.charts.client.core.xy.axis.AxisBuilder;
import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.charts.client.core.xy.bar.BarChart;
import com.ait.lienzo.charts.client.core.xy.bar.BarChartLabel;
import com.ait.lienzo.charts.client.core.xy.bar.BarChartTooltip;
import com.ait.lienzo.charts.shared.core.types.ChartDirection;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Rectangle;

public class BarChartClearAnimation extends AbstractChartAnimation
{
    public BarChartClearAnimation(BarChart barChart, double width, double height, AnimationTweener tweener, double duration, IAnimationCallback callback)
    {
        super(barChart, width, height, tweener, duration, callback);
        init(width, height);
    }

    protected BarChart getBarChart()
    {
        return (BarChart) getNode();
    }

    protected boolean isVertical()
    {
        return getBarChart().isVertical();
    }

    private void init(final double chartWidth, final double chartHeight)
    {

        // Legend & tooltip.
        final ChartLegend legend = getBarChart().getChartLegend();
        final BarChartTooltip tooltip = getBarChart().getChartTooltip();
        if (legend != null) legend.clear();
        if (tooltip != null) tooltip.clear();

        final List<BarChartLabel> seriesLabels = getBarChart().getSeriesLabels();
        final AxisBuilder categoriesAxisBuilder = getBarChart().getCategoriesAxisBuilder();
        final List<AxisLabel> labels = categoriesAxisBuilder.getLabels();
        final List<Line> valuesAxisIntervals = getBarChart().getValuesAxisIntervals();
        final List<BarChartLabel> valuesLabels = getBarChart().getValuesLabels();
        final AxisBuilder valuesAxisBuilder = getBarChart().getValuesAxisBuilder();
        final Map<String, List<Rectangle>> seriesValues = getBarChart().getSeriesValues();
        XYChartSeries[] series = getBarChart().getData().getSeries();

        // Categories labels.
        if (seriesLabels != null)
        {
            for (BarChartLabel label : seriesLabels)
            {
                add(label, buildAnimationProperties(0d));
            }
        }
        // Values labels.
        if (valuesLabels != null)
        {
            for (BarChartLabel label : valuesLabels)
            {
                add(label, buildAnimationProperties(0d));
            }
        }
        // Create the nodes' animations.
        final double yClearPos = chartHeight;
        final double xClearPos = ChartDirection.POSITIVE.equals(getBarChart().getDirection()) ? 0 : chartWidth;
        // Apply animation values axis intervals.
        if (valuesAxisIntervals != null)
        {
            for (final Line line : valuesAxisIntervals)
            {
                if (line != null)
                {
                    final double clearDiff = isVertical() ? (yClearPos - line.getPoints().get(1).getY()) : (xClearPos - line.getPoints().get(1).getX());
                    if (isVertical()) add(line, buildAnimationProperties(null, clearDiff, null, null));
                    else add(line, buildAnimationProperties(clearDiff, null, null, null));
                }
            }
        }
        AnimationProperties animationProperties2 = new AnimationProperties();
        animationProperties2.push(AnimationProperty.Properties.Y(yClearPos));
        animationProperties2.push(AnimationProperty.Properties.HEIGHT(0d));

        // Apply animation to values.
        if (seriesValues != null)
        {
            for (Map.Entry<String, List<Rectangle>> entry : seriesValues.entrySet())
            {
                for (Rectangle rectangle : entry.getValue())
                {
                    if (isVertical()) add(rectangle, buildAnimationProperties(null, yClearPos, null, 0d));
                    else add(rectangle, buildAnimationProperties(null, null, xClearPos, null));
                }
            }
        }
        // Create axis titles' animations.
        if (!getBarChart().getCategoriesAxisTitle().isEmpty()) add(getBarChart().getCategoriesAxisTitle().get(0), buildAnimationProperties(null, null, 0d, 0d));
        if (!getBarChart().getValuesAxisTitle().isEmpty()) add(getBarChart().getValuesAxisTitle().get(0), buildAnimationProperties(null, null, 0d, 0d));
    }

    @Override
    public IAnimation doClose()
    {
        getBarChart().clear();
        super.doClose();
        return this;
    }
}
