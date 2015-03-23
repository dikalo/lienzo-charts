
package com.ait.lienzo.charts.client.core.xy.bar.animation;

import com.ait.lienzo.charts.client.core.AbstractChart;
import com.ait.lienzo.charts.client.core.animation.PointsAnimationProperty;
import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.charts.client.core.xy.bar.BarChart;
import com.ait.lienzo.charts.client.core.xy.bar.BarChartLabel;
import com.ait.lienzo.charts.shared.core.types.LabelsPosition;
import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.IColor;

/**
 * <p>Creates the Bar Chart children shapes by setting initial positions/sizes and adding animations to target positions/sizes.</p> 
 */
public class BarChartCreateAnimation extends AbstractBarChartAnimation
{
    
    public BarChartCreateAnimation(final BarChart barChart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(barChart, barChart.getChartWidth(), barChart.getChartHeight(), tweener, duration, callback);
        add(barChart, buildAnimationProperties(null, null, barChart.getChartWidth(), barChart.getChartHeight()));
    }

    @Override
    protected void init(final AbstractChart<?> node) {
        super.init(node);

        // Build chart children.
        node.draw();

        calculate(m_width, m_height);
    }

    @Override
    protected void doAnimateChartTitle(Text chartTitle, Double x, Double y)
    {
        createTitle(chartTitle, x, y);
    }

    @Override
    protected void doAnimateCategoriesAxisTitle(final Text categoriesAxisTitle, final Double x, final Double y)
    {
        createTitle(categoriesAxisTitle, x, y);
    }

    @Override
    protected void doAnimateValuesAxisTitle(final Text valuesAxisTitle, final Double x, final Double y)
    {
        createTitle(valuesAxisTitle, x, y);
    }

    @Override
    protected void doAnimateCategoriesAxisIntervals(final BarChartLabel chartLabel, final AxisLabel axisLabel, final Double x, final Double y)
    {
        // Init label position.
        createCategoriesAxisLabel(chartLabel);
        
        // Text.
        chartLabel.set(axisLabel);
        
        // Add animation to final position.
        add(chartLabel, buildAnimationProperties(x, y));
    }

    private void createCategoriesAxisLabel(final BarChartLabel chartLabel) {
        
        // Initial position.
        if (isVertical())
        {
            final LabelsPosition valuesAxisLabelPosition = getBarChart().getValuesAxisLabelsPosition();
            final double lx = LabelsPosition.RIGHT.equals(valuesAxisLabelPosition) ? getBarChart().getChartWidth() : 0;
            chartLabel.setX(lx).setY(0);
        }
        else 
        {
            final LabelsPosition valuesAxisLabelPosition = getBarChart().getValuesAxisLabelsPosition();
            final double ly = LabelsPosition.BOTTOM.equals(valuesAxisLabelPosition) ? getBarChart().getChartHeight() : 0;
            chartLabel.setX(0).setY(ly);
        }
    
    }

    @Override
    protected void doAnimateValuesAxisIntervals(final Line valueAxisInterval, final double p00, final double p01, final double p10, final double p11)
    {
        final AnimationProperties animationProperties = new AnimationProperties();
        animationProperties.push(new PointsAnimationProperty(new Point2D(p00, p01), new Point2D(p10, p11), Attribute.POINTS));
        add(valueAxisInterval, animationProperties);
    }

    @Override
    protected void doAnimateValuesAxisIntervals(final BarChartLabel chartLabel, final AxisLabel axisLabel, final Double x, final Double y)
    {
        // Init label position.
        createValuesAxisLabel(chartLabel);

        // Text.
        chartLabel.set(axisLabel);

        // Add animation to final position.
        add(chartLabel, buildAnimationProperties(x, y));
    }

    private void createValuesAxisLabel(final BarChartLabel chartLabel) {

        // Initial position.
        if (isVertical())
        {
            final LabelsPosition categoryAxisLabelPosition = getBarChart().getCategoriesAxisLabelsPosition();
            final double ly = LabelsPosition.BOTTOM.equals(categoryAxisLabelPosition) ? getBarChart().getChartHeight() : 0;
            chartLabel.setX(0).setY(ly);
        } 
        else 
        {
            final LabelsPosition categoryAxisLabelPosition = getBarChart().getCategoriesAxisLabelsPosition();
            final double lx = LabelsPosition.RIGHT.equals(categoryAxisLabelPosition) ? getBarChart().getChartWidth() : 0;
            chartLabel.setX(lx).setY(0);
        }

    }

    @Override
    protected void doAnimateValues(final Rectangle bar, final Double x, final Double y, final Double w, final Double h, final IColor color, final Double alpha, final boolean isSeriesNew)
    {
        // Initial bar positions.
        final double initialX = isVertical() ? x : 0d;
        final double initialY = isVertical() ? m_height : y;
        bar.setX(initialX);
        bar.setY(initialY);

        final AnimationProperties animationProperties = buildAnimationProperties(x, y, w, h);
        animationProperties.push(AnimationProperty.Properties.FILL_COLOR(color));
        animationProperties.push(AnimationProperty.Properties.ALPHA(alpha));
        add(bar, animationProperties);
    }
    
    private void createTitle(final Shape shape, final Double x, final Double y) {
        // Position.
        if (x != null) shape.setX(x);
        if (y != null) shape.setY(y);

        // Initial alpha.
        shape.setAlpha(0d);

        // Animation to final alpha.
        add(shape, buildAnimationProperties(1d));
        
    }
}
