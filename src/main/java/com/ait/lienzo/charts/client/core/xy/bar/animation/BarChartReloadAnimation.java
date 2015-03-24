/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   
   Author: Roger Martinez - Red Hat
 */

package com.ait.lienzo.charts.client.core.xy.bar.animation;

import com.ait.lienzo.charts.client.core.AbstractChart;
import com.ait.lienzo.charts.client.core.xy.XYChartData;
import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.charts.client.core.xy.bar.BarChart;
import com.ait.lienzo.charts.client.core.xy.bar.BarChartLabel;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.IColor;

import java.util.Set;

/**
 * <p>Reload the Bar Chart children shapes by animating new or updates dates children's shapes to its positions/sizes .</p> 
 */
public final class BarChartReloadAnimation extends AbstractBarChartAnimation
{
    public BarChartReloadAnimation(final BarChart barChart, final XYChartData data, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(barChart, barChart.getChartWidth(), barChart.getChartHeight(), tweener, duration, callback);
        add(barChart, buildAnimationProperties(null, null, barChart.getChartWidth(), barChart.getChartHeight()));
        barChart.setData(data);
        reloadBuilders(data, barChart.getChartWidth(), barChart.getChartHeight());
        calculate(m_width, m_height);
    }

    @Override
    protected void init(final AbstractChart<?> node)
    {
        // Do not calculate on default init method, calculate after performing reloadBuilders on constructor method, never before.
    }

    protected void reloadBuilders(final XYChartData data, final double chartWidth, final double chartHeight)
    {
        Set<String> currentSeries = null;
        if (!getBarChart().getSeriesValues().isEmpty()) currentSeries = getBarChart().getSeriesValues().keySet();
        getBarChart().getCategoriesAxisBuilder().reload(data, currentSeries, isVertical() ? chartWidth : chartHeight);
        getBarChart().getValuesAxisBuilder().reload(data, currentSeries, isVertical() ? chartHeight : chartWidth);
    }

    @Override
    protected void doAnimateChartTitle(Text chartTitle, Double x, Double y)
    {
        // Do nothing.
    }

    @Override
    protected void doAnimateCategoriesAxisTitle(final Text categoriesAxisTitle, final Double x, final Double y)
    {
        // Do nothing.
    }

    @Override
    protected void doAnimateValuesAxisTitle(final Text valuesAxisTitle, final Double x, final Double y)
    {
        // Do nothing.
    }

    @Override
    protected void doAnimateCategoriesAxisIntervals(final BarChartLabel chartLabel, final AxisLabel axisLabel, final Double x, final Double y)
    {
        // Text.
        chartLabel.set(axisLabel);

        // Add animation to final position.
        add(chartLabel, buildAnimationProperties(x, y));
    }

    @Override
    protected void doAnimateValuesAxisIntervals(final Line valueAxisInterval, final double p00, final double p01, final double p10, final double p11)
    {
        // Do nothing.
    }

    @Override
    protected void doAnimateValuesAxisIntervals(final BarChartLabel chartLabel, final AxisLabel axisLabel, final Double x, final Double y)
    {
        // Text.
        chartLabel.set(axisLabel);

        // Add animation to final position.
        add(chartLabel, buildAnimationProperties(x, y));
    }

    @Override
    protected void doAnimateValues(final Rectangle bar, final Double x, final Double y, final Double w, final Double h, final IColor color, final Double alpha, final boolean isSeriesNew)
    {
        if (isSeriesNew)
        {
            // Initial bar positions.
            final double initialX = isVertical() ? x : 0d;
            final double initialY = isVertical() ? m_height : y;
            bar.setX(initialX);
            bar.setY(initialY);
        }
        AnimationProperties animationProperties = buildAnimationProperties(x, y, w, h);
        animationProperties.push(AnimationProperty.Properties.FILL_COLOR(color));
        animationProperties.push(AnimationProperty.Properties.ALPHA(alpha));
        add(bar, animationProperties);
    }
}
