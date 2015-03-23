
package com.ait.lienzo.charts.client.core.animation;

import com.ait.lienzo.charts.client.core.AbstractChart;
import com.ait.lienzo.charts.client.core.legend.ChartLegend;
import com.ait.lienzo.charts.client.core.resizer.ChartResizer;
import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.animation.*;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.IColor;

public abstract class AbstractChartAnimation extends StackedTweeningAnimation
{
    protected double m_width;

    protected double m_height;

    public AbstractChartAnimation(final AbstractChart<?> node, final double chartWidth, final double chartHeight, AnimationTweener tweener, double duration, IAnimationCallback callback)
    {
        super(node, tweener, duration, callback);
        this.m_width = chartWidth;
        this.m_height = chartHeight;

        setNode(node);
        
        init(node);
    }

    protected AbstractChart<?> getChart()
    {
        return (AbstractChart<?>) getNode();
    }
    
    protected void init(final AbstractChart<?> node) {
        // Initialize.        
    }

    protected void calculate(final double w, final double h)
    {
        calculateChartTitle(w, h);
        calculateChartLegend(w, h);
    }

    protected void calculateChartLegend(final double w, final double h)
    {
        final Double[] legendPositions = getChart().getLegendPositions(w, h);
        if (legendPositions != null) doAnimateChartLegend(getChart().getChartLegend(), legendPositions[0], legendPositions[1]);
    }

    protected void doAnimateChartLegend(final ChartLegend chartLegend, final Double x, final Double y)
    {
        chartLegend.setX(x).setY(y);
    }

    protected void calculateChartTitle(final double w, final double h)
    {
        doAnimateChartTitle(getChart().getChartTitle(), w / 2, 10d);
    }

    protected void doAnimateChartTitle(final Text chartTitle, final Double x, final Double y)
    {
        chartTitle.setX(x).setY(y);
    }

    @Override
    public IAnimation doClose() {
        ChartResizer resizer = getChart().getChartResizer();
        if (resizer != null) resizer.moveToTop();
        return super.doClose();
    }

    protected static AnimationProperties buildAnimationProperties(final Double x, final Double y)
    {
        if (x == null && y == null) return null;
        AnimationProperties animationProperties = new AnimationProperties();
        if (x != null) animationProperties.push(AnimationProperty.Properties.X(x));
        if (y != null) animationProperties.push(AnimationProperty.Properties.Y(y));
        return animationProperties;
    }

    protected static AnimationProperties buildAnimationProperties(final Double x, final Double y, final Double w, final Double h)
    {
        if (x == null && y == null && w == null && h == null) return null;
        AnimationProperties animationProperties = new AnimationProperties();
        if (x != null) animationProperties.push(AnimationProperty.Properties.X(x));
        if (y != null) animationProperties.push(AnimationProperty.Properties.Y(y));
        if (w != null) animationProperties.push(AnimationProperty.Properties.WIDTH(w));
        if (h != null) animationProperties.push(AnimationProperty.Properties.HEIGHT(h));
        return animationProperties;
    }

    public static AnimationProperties buildAnimationProperties(final Double alpha)
    {
        if (alpha == null) return null;
        AnimationProperties animationProperties = new AnimationProperties();
        if (alpha != null) animationProperties.push(AnimationProperty.Properties.ALPHA(alpha));
        return animationProperties;
    }

    protected void setShapeAttributes(final Shape<?> shape, final Double x, final Double y)
    {
        setShapeAttributes(shape, x, y, null, null, null, null);
    }

    protected void setShapeAttributes(final Shape<?> shape, final Double x, final Double y, final Double w, final Double h, IColor color, final Double alpha)
    {
        if (shape != null)
        {
            if (x != null) shape.setX(x);
            if (y != null) shape.setY(y);
            if (w != null) shape.getAttributes().setWidth(w);
            if (h != null) shape.getAttributes().setHeight(h);
            if (color != null) shape.setFillColor(color);
            if (alpha != null) shape.setAlpha(alpha);
        }
    }

    protected void setShapeAttributes(final Shape<?> shape, final Double x, final Double y, final Double alpha)
    {
        if (shape != null)
        {
            if (x != null) shape.setX(x);
            if (y != null) shape.setY(y);
            if (alpha != null) shape.setAlpha(alpha);
        }
    }

    protected void setShapeCircularAttributes(final Shape<?> shape, final Double radius, final Double startAngle, final Double endAngle)
    {
        if (shape != null)
        {
            if (radius != null) shape.getAttributes().put(Attribute.RADIUS.getProperty(), radius);
            if (startAngle != null) shape.getAttributes().put(Attribute.START_ANGLE.getProperty(), startAngle);
            if (endAngle != null) shape.getAttributes().put(Attribute.END_ANGLE.getProperty(), endAngle);
        }
    }
}