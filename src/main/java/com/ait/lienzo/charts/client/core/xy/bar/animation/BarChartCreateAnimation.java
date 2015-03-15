
package com.ait.lienzo.charts.client.core.xy.bar.animation;

import com.ait.lienzo.charts.client.core.animation.PointsAnimationProperty;
import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.charts.client.core.xy.bar.BarChart;
import com.ait.lienzo.charts.client.core.xy.bar.BarChartLabel;
import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.IColor;

public class BarChartCreateAnimation extends AbstractBarChartAnimation
{
    public BarChartCreateAnimation(BarChart barChart, double chartWidth, double chartHeight, AnimationTweener tweener, double duration, IAnimationCallback callback)
    {
        super(barChart, chartWidth, chartHeight, tweener, duration, callback);
        
        if (doCalculateOnInit()) calculate(m_width, m_height);
        
        add(barChart, buildAnimationProperties(null, null, chartWidth, chartHeight));
    }

    protected boolean doCalculateOnInit()
    {
        return true;
    }

    @Override
    protected void doAnimateChartTitle(Text chartTitle, Double x, Double y)
    {
        add(chartTitle, buildAnimationProperties(x, y, null, null));
    }

    @Override
    protected void doAnimateCategoriesAxisTitle(final Text categoriesAxisTitle, final Double x, final Double y)
    {
        add(categoriesAxisTitle, buildAnimationProperties(x, y, null, null));
    }

    @Override
    protected void doAnimateValuesAxisTitle(final Text valuesAxisTitle, final Double x, final Double y)
    {
        add(valuesAxisTitle, buildAnimationProperties(x, y, null, null));
    }

    @Override
    protected void doAnimateCategoriesAxisIntervals(final BarChartLabel chartLabel, final AxisLabel axisLabel, final Double x, final Double y)
    {
        //TODO: Use ComposedTweeningAnimation#add()
        chartLabel.setAttributes(axisLabel, x, y, null, null, true);
    }

    @Override
    protected void doAnimateValuesAxisIntervals(final Line valueAxisInterval, final double p00, final double p01, final double p10, final double p11)
    {
        AnimationProperties animationProperties = new AnimationProperties();
        animationProperties.push(new PointsAnimationProperty(new Point2D(p00, p01), new Point2D(p10, p11), Attribute.POINTS));
        add(valueAxisInterval, animationProperties);
        // valueAxisInterval.setPoints(new Point2DArray(new Point2D(p00, p01), new Point2D(p10, p11)));
    }

    @Override
    protected void doAnimateValuesAxisIntervals(final BarChartLabel chartLabel, final AxisLabel axisLabel, final Double x, final Double y)
    {
        //TODO: Use ComposedTweeningAnimation#add()
        chartLabel.setAttributes(axisLabel, x, y, null, null, true);
    }

    @Override
    protected void doAnimateValues(final Rectangle bar, final Double x, final Double y, final Double w, final Double h, final IColor color, final Double alpha, final boolean isSeriesNew)
    {
        if (isVertical())
        {
            final double initialX = 0d;
            final double initialY = m_height;
            bar.setX(initialX);
            bar.setY(initialY);
        }
        AnimationProperties animationProperties = buildAnimationProperties(x, y, w, h);
        animationProperties.push(AnimationProperty.Properties.FILL_COLOR(color));
        animationProperties.push(AnimationProperty.Properties.ALPHA(alpha));
        add(bar, animationProperties);
    }
}
