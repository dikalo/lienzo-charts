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

import com.ait.lienzo.charts.client.core.legend.ChartLegend;
import com.ait.lienzo.charts.client.core.xy.animation.XYChartAnimation;
import com.ait.lienzo.charts.client.core.xy.XYChartSeries;
import com.ait.lienzo.charts.client.core.xy.axis.AxisBuilder;
import com.ait.lienzo.charts.client.core.xy.axis.AxisValue;
import com.ait.lienzo.charts.client.core.xy.line.LineChart;
import com.ait.lienzo.charts.shared.core.types.ChartDirection;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.IColor;

import java.util.List;
import java.util.Map;

public abstract class AbstractLineChartAnimation extends XYChartAnimation
{
    public AbstractLineChartAnimation(final LineChart lineChart, final double chartWidth, final double chartHeight, AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(lineChart, chartWidth, chartHeight, tweener, duration, callback);
    }

    protected LineChart getLineChart()
    {
        return (LineChart) getNode();
    }

    protected void calculateValues(final double w, final double h)
    {
        final AxisBuilder<?> categoriesAxisBuilder = getLineChart().getCategoriesAxisBuilder();
        final XYChartSeries[] series = getLineChart().getData().getSeries();
        final ChartLegend legend = getLineChart().getChartLegend();

        // Find removed series in order to remove line instances.
        for (String removedSeriesName : categoriesAxisBuilder.getDataSummary().getRemovedSeries())
        {
            // TODO: Remove series values should be done only in a LineChartReloadAnimation?
            getLineChart().removeSeriesValues(removedSeriesName);
            if (legend != null) legend.remove(removedSeriesName).build();
        }
        categoriesAxisBuilder.getDataSummary().getRemovedSeries().clear();

        // Iterate over all series.
        for (int numSeries = 0; numSeries < series.length; numSeries++)
        {
            final XYChartSeries _series = series[numSeries];
            if (_series != null)
            {
                // If a new series is added, draw new line instances.
                boolean isSeriesNew = false;
                if (categoriesAxisBuilder.getDataSummary().getAddedSeries().contains(_series.getName()))
                {
                    // TODO: Build series values should be done only in a LineChartReloadAnimation?
                    getLineChart().buildSeriesValues(_series, numSeries);
                    if (legend != null) legend.add(new ChartLegend.ChartLegendEntry(_series.getName(), _series.getColor())).build();
                    categoriesAxisBuilder.getDataSummary().getAddedSeries().remove(_series.getName());
                    isSeriesNew = true;
                }
                if (isVertical()) calculateValuesForVertical(_series, numSeries, w, h, isSeriesNew);
                else calculateValuesForHorizontal(_series, numSeries, w, h, isSeriesNew);
            }
        }
    }

    protected AbstractLineChartAnimation calculateValuesForVertical(final XYChartSeries series, final int numSeries, final Double width, final Double height, boolean isSeriesNew)
    {
        final Map<String, List<Line>> seriesValues = getLineChart().getSeriesValues();
        final Map<String, List<Circle>> seriesCircles = getLineChart().getSeriesPoints();
        final AxisBuilder<?> categoriesAxisBuilder = getLineChart().getCategoriesAxisBuilder();
        final AxisBuilder<?> valuesAxisBuilder = getLineChart().getValuesAxisBuilder();

        // Rebuild line for series values
        final List<? extends AxisValue<?>> valuesAxisValues = valuesAxisBuilder.getValues(series.getValuesAxisProperty());
        final List<? extends AxisValue<?>> categoryAxisValues = categoriesAxisBuilder.getValues(getLineChart().getData().getCategoryAxisProperty());
        final List<Line> lines = seriesValues.get(series.getName());
        final List<Circle> circles = seriesCircles.get(series.getName());
        Double lastX = null;
        Double lastY = null;
        if (categoryAxisValues != null && categoryAxisValues.size() > 0)
        {
            for (int i = 0; i < categoryAxisValues.size(); i++)
            {
                final AxisValue<?> categoryAxisValue = categoryAxisValues.get(i);
                final double xAxisValuePosition = categoryAxisValue.getPosition();
                final AxisValue<?> valueAxisValue = valuesAxisValues.get(i);
                final double yAxisValuePosition = valueAxisValue.getPosition();

                // Calculate positions.
                Double x = xAxisValuePosition;
                Double y = height - yAxisValuePosition;
                if (lastX == null && lastY == null) {
                    lastY = y;
                    lastX = x;
                    x = null;
                    y = null;
                }

                /*
                // TODO: If current line is not in Y axis intervals (max / min values), resize it and apply an alpha.
                boolean isOutOfChartArea = y < 0;
                if (isOutOfChartArea)
                {
                    alpha = 0.1d;
                    barHeight = height;
                    y = 0;
                }
                */

                // Animate line shapes.
                if (lastX != null && lastY != null && x != null && y != null) {
                    final Point2D p1 = new Point2D(lastX, lastY);
                    final Point2D p2 = new Point2D(x, y);
                    final Line line = lines.get(i - 1);
                    line.moveToTop();
                    line.setDraggable(false);

                    doAnimateValues(line, p1, p2, width, height, series.getColor(), isSeriesNew);
                    
                    lastX = x;
                    lastY = y;
                }

                // Animate circle shapes.
                final Circle circle = circles.get(i);
                doAnimateValues(circle, lastX, lastY, width, height, series.getColor(), isSeriesNew);
            }
        }

        return this;
    }

    protected AbstractLineChartAnimation calculateValuesForHorizontal(final XYChartSeries series, final int numSeries, Double width, final Double height, boolean isSeriesNew)
    {
        final Map<String, List<Line>> seriesValues = getLineChart().getSeriesValues();
        final Map<String, List<Circle>> seriesCircles = getLineChart().getSeriesPoints();
        final AxisBuilder<?> categoriesAxisBuilder = getLineChart().getCategoriesAxisBuilder();
        final AxisBuilder<?> valuesAxisBuilder = getLineChart().getValuesAxisBuilder();

        // Rebuild line for series values
        final List<? extends AxisValue<?>> valuesAxisValues = valuesAxisBuilder.getValues(series.getValuesAxisProperty());
        final List<? extends AxisValue<?>> categoryAxisValues = categoriesAxisBuilder.getValues(getLineChart().getData().getCategoryAxisProperty());
        final List<Line> lines = seriesValues.get(series.getName());
        final List<Circle> circles = seriesCircles.get(series.getName());
        Double lastX = null;
        Double lastY = null;
        if (categoryAxisValues != null && categoryAxisValues.size() > 0)
        {
            for (int i = 0; i < categoryAxisValues.size(); i++)
            {
                final AxisValue<?> categoryAxisValue = categoryAxisValues.get(i);
                final double yAxisValuePosition = categoryAxisValue.getPosition();
                final AxisValue<?> valueAxisValue = valuesAxisValues.get(i);
                final double xAxisValuePosition = valueAxisValue.getPosition();

                // Calculate positions.
                Double x = getLineChart().getDirection().equals(ChartDirection.POSITIVE) ? xAxisValuePosition : 0d;
                Double y = yAxisValuePosition;
                if (lastX == null && lastY == null) {
                    lastX = x;
                    lastY = y;
                    x = null;
                    y = null;
                }

                /*
                // TODO: // If current line is not in Y axis intervals (max / min values), resize it and apply an alpha.
                double lastXIntervalPosition = xAxisLabels.get(xAxisLabels.size() - 1).getPosition();
                boolean isOutOfChartArea = barWidth > lastXIntervalPosition;
                if (isOutOfChartArea)
                {
                    alpha = 0.1d;
                    barWidth = width;
                }
                */

                // Animate line shapes.
                if (lastX != null && lastY != null && x != null && y != null) {
                    final Point2D p1 = new Point2D(lastX, lastY);
                    final Point2D p2 = new Point2D(x, y);
                    final Line line = lines.get(i - 1);
                    line.moveToTop();
                    line.setDraggable(false);

                    doAnimateValues(line, p1, p2, width, height, series.getColor(), isSeriesNew);

                    lastX = x;
                    lastY = y;
                }

                // Animate circle shapes.
                final Circle circle = circles.get(i);
                doAnimateValues(circle, lastX, lastY, width, height, series.getColor(), isSeriesNew);
            }
        }
        
        return this;
    }

    protected abstract void doAnimateValues(final Line line, final Point2D p1, final Point2D p2, final Double w, final Double h, IColor color, final boolean isSeriesNew);

    protected abstract void doAnimateValues(final Circle circle, final Double x, final Double y, final Double w, final Double h, IColor color, final boolean isSeriesNew);
}