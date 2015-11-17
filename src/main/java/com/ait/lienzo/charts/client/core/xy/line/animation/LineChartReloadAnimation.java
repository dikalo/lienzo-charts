/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.charts.client.core.xy.line.animation;

import com.ait.lienzo.charts.client.core.AbstractChart;
import com.ait.lienzo.charts.client.core.animation.PointsAnimationProperty;
import com.ait.lienzo.charts.client.core.xy.XYChartData;
import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.charts.client.core.xy.label.XYChartLabel;
import com.ait.lienzo.charts.client.core.xy.line.LineChart;
import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.IColor;

import java.util.Set;

/**
 * <p>Reload the Line Chart children shapes by animating new or updates dates children's shapes to its positions/sizes .</p> 
 */
public final class LineChartReloadAnimation extends AbstractLineChartAnimation
{
    public LineChartReloadAnimation(final LineChart lineChart, final XYChartData data, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(lineChart, lineChart.getChartWidth(), lineChart.getChartHeight(), tweener, duration, callback);
        add(lineChart, buildAnimationProperties(null, null, lineChart.getChartWidth(), lineChart.getChartHeight()));
        lineChart.setData(data);
        reloadBuilders(data, lineChart.getChartWidth(), lineChart.getChartHeight());
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
        if (!getLineChart().getSeriesValues().isEmpty()) currentSeries = getLineChart().getSeriesValues().keySet();
        getLineChart().getCategoriesAxisBuilder().reload(data, currentSeries, isVertical() ? chartWidth : chartHeight);
        getLineChart().getValuesAxisBuilder().reload(data, currentSeries, isVertical() ? chartHeight : chartWidth);
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
    protected void doAnimateCategoriesAxisIntervals(final XYChartLabel chartLabel, final AxisLabel axisLabel, final Double x, final Double y)
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
    protected void doAnimateValuesAxisIntervals(final XYChartLabel chartLabel, final AxisLabel axisLabel, final Double x, final Double y)
    {
        // Text.
        chartLabel.set(axisLabel);

        // Add animation to final position.
        add(chartLabel, buildAnimationProperties(x, y));
    }

    @Override
    protected void doAnimateValues(Line line, Point2D p1, Point2D p2, Double w, Double h, IColor color, boolean isSeriesNew) {
        final AnimationProperties animationProperties = new AnimationProperties();
        animationProperties.push(AnimationProperty.Properties.STROKE_COLOR(color));
        animationProperties.push(new PointsAnimationProperty(p1, p2, Attribute.POINTS));
        add(line, animationProperties);
    }

    @Override
    protected void doAnimateValues(final Circle circle, final Double x, final Double y, final Double w, final Double h, final IColor color, final boolean isSeriesNew)
    {
        /* TODO
        if (isSeriesNew)
        {
            // Initial bar positions.
            final double initialX = isVertical() ? x : 0d;
            final double initialY = isVertical() ? m_height : y;
            bar.setX(initialX);
            bar.setY(initialY);
        }*/
        AnimationProperties animationProperties = buildAnimationProperties(x, y);
        animationProperties.push(AnimationProperty.Properties.FILL_COLOR(color));
        add(circle, animationProperties);
    }
}
