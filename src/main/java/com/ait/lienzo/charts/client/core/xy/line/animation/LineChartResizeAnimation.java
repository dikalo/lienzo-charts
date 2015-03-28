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

package com.ait.lienzo.charts.client.core.xy.line.animation;

import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.charts.client.core.xy.label.XYChartLabel;
import com.ait.lienzo.charts.client.core.xy.line.LineChart;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.IColor;

public class LineChartResizeAnimation extends AbstractLineChartAnimation
{
    public LineChartResizeAnimation(final LineChart lineChart, final double chartWidth, final double chartHeight, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(lineChart, chartWidth, chartHeight, tweener, duration, callback);
        add(lineChart, buildAnimationProperties(null, null, chartWidth, chartHeight));
        reloadBuilders(chartWidth, chartHeight);
    }

    protected void reloadBuilders(final double chartWidth, final double chartHeight)
    {
        getLineChart().getCategoriesAxisBuilder().reload(isVertical() ? chartWidth : chartHeight);
        getLineChart().getValuesAxisBuilder().reload(isVertical() ? chartHeight : chartWidth);
    }

    @Override
    protected boolean apply(final NodeAnimation nodeAnimation, final double percent)
    {
        boolean apply = super.apply(nodeAnimation, percent);
        final double w = getLineChart().getChartWidth();
        final double h = getLineChart().getChartHeight();
        reloadBuilders(w, h);
        calculate(w, h);
        return apply;
    }

    @Override
    protected void doAnimateCategoriesAxisTitle(final Text categoriesAxisTitle, final Double x, final Double y)
    {
        setShapeAttributes(categoriesAxisTitle, x, y);
    }

    @Override
    protected void doAnimateValuesAxisTitle(final Text valuesAxisTitle, final Double x, final Double y)
    {
        setShapeAttributes(valuesAxisTitle, x, y);
    }

    @Override
    protected void doAnimateCategoriesAxisIntervals(final XYChartLabel chartLabel, final AxisLabel axisLabel, final Double x, final Double y)
    {
        chartLabel.set(axisLabel);
        chartLabel.setX(x).setY(y);
    }

    @Override
    protected void doAnimateValuesAxisIntervals(final Line valueAxisInterval, final double p00, final double p01, final double p10, final double p11)
    {
        if (valueAxisInterval != null) valueAxisInterval.setPoints(new Point2DArray(new Point2D(p00, p01), new Point2D(p10, p11)));
    }

    @Override
    protected void doAnimateValuesAxisIntervals(final XYChartLabel chartLabel, final AxisLabel axisLabel, final Double x, final Double y)
    {
        chartLabel.set(axisLabel);
        chartLabel.setX(x).setY(y);
    }

    @Override
    protected void doAnimateValues(Line line, Point2D p1, Point2D p2, Double w, Double h, IColor color, boolean isSeriesNew) {
        line.setPoint2DArray(new Point2DArray(p1, p2)).setStrokeColor(color);
    }

    @Override
    protected void doAnimateValues(final Circle circle, final Double x, final Double y, final Double w, final Double h, final IColor color, final boolean isSeriesNew)
    {
        setShapeAttributes(circle, x, y);
        circle.setFillColor(color);
    }
    
}
