
package com.ait.lienzo.charts.client.core.xy.bar.animation;

import com.ait.lienzo.charts.client.core.xy.XYChartData;
import com.ait.lienzo.charts.client.core.xy.bar.BarChart;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.IColor;

import java.util.Set;

public final class BarChartReloadAnimation extends BarChartCreateAnimation
{
    public BarChartReloadAnimation(final BarChart barChart, final XYChartData data, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(barChart, barChart.getChartWidth(), barChart.getChartHeight(), tweener, duration, callback);
        reloadBuilders(data, barChart.getChartWidth(), barChart.getChartHeight());
        calculate(m_width, m_height);
    }

    @Override
    protected boolean doCalculateOnInit()
    {
        return false;
    }

    protected void reloadBuilders(final XYChartData data, final double chartWidth, final double chartHeight)
    {
        Set<String> currentSeries = null;
        if (!getBarChart().getSeriesValues().isEmpty()) currentSeries = getBarChart().getSeriesValues().keySet();
        getBarChart().getCategoriesAxisBuilder().reload(data, currentSeries, isVertical() ? chartWidth : chartHeight);
        getBarChart().getValuesAxisBuilder().reload(data, currentSeries, isVertical() ? chartHeight : chartWidth);
    }

    @Override
    protected void doAnimateValues(final Rectangle bar, final Double x, final Double y, final Double w, final Double h, final IColor color, final Double alpha, final boolean isSeriesNew)
    {
        AnimationProperties animationProperties = buildAnimationProperties(x, y, w, h);
        animationProperties.push(AnimationProperty.Properties.FILL_COLOR(color));
        animationProperties.push(AnimationProperty.Properties.ALPHA(alpha));
        add(bar, animationProperties);
    }
}
