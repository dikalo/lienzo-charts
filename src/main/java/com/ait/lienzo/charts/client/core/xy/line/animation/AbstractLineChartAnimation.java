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

import com.ait.lienzo.charts.client.core.legend.ChartLegend;
import com.ait.lienzo.charts.client.core.xy.XYChartAnimation;
import com.ait.lienzo.charts.client.core.xy.XYChartSeries;
import com.ait.lienzo.charts.client.core.xy.axis.AxisBuilder;
import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.charts.client.core.xy.axis.AxisValue;
import com.ait.lienzo.charts.client.core.xy.bar.BarChart;
import com.ait.lienzo.charts.client.core.xy.line.LineChart;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Rectangle;
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
        final AxisBuilder categoriesAxisBuilder = getLineChart().getCategoriesAxisBuilder();
        XYChartSeries[] series = getLineChart().getData().getSeries();
        final ChartLegend legend = getLineChart().getChartLegend();

        // Find removed series in order to remove bar rectangle instances.
        for (String removedSerieName : categoriesAxisBuilder.getDataSummary().getRemovedSeries())
        {
            // TODO: Remove serie values should be done only in a BarChartReloadAnimation?
            getLineChart().removeSeriesValues(removedSerieName);
            if (legend != null) legend.remove(removedSerieName).build();
        }
        categoriesAxisBuilder.getDataSummary().getRemovedSeries().clear();

        // Iterate over all series.
        for (int numSerie = 0; numSerie < series.length; numSerie++)
        {
            final XYChartSeries serie = series[numSerie];
            if (serie != null)
            {
                // If a new serie is added, draw new bar rectangle instances.
                boolean isSeriesNew = false;
                if (categoriesAxisBuilder.getDataSummary().getAddedSeries().contains(serie.getName()))
                {
                    // TODO: Build serie values should be done only in a BarChartReloadAnimation?
                    getLineChart().buildSeriesValues(serie, numSerie);
                    if (legend != null) legend.add(new ChartLegend.ChartLegendEntry(serie.getName(), serie.getColor())).build();
                    categoriesAxisBuilder.getDataSummary().getAddedSeries().remove(serie.getName());
                    isSeriesNew = true;
                }
                if (isVertical()) calculateValuesForVertical(serie, numSerie, w, h, isSeriesNew);
                else calculateValuesForHorizontal(serie, numSerie, w, h, isSeriesNew);
            }
        }
    }

    protected AbstractLineChartAnimation calculateValuesForVertical(final XYChartSeries serie, final int numSerie, final Double width, final Double height, boolean isSeriesNew)
    {
        final double ml = getLineChart().getMarginLeft();
        final double mr = getLineChart().getMarginRight();
        final double mt = getLineChart().getMarginTop();
        final double mb = getLineChart().getMarginBottom();
        final Map<String, List<Line>> seriesValues = getLineChart().getSeriesValues();
        final AxisBuilder categoriesAxisBuilder = getLineChart().getCategoriesAxisBuilder();
        final AxisBuilder valuesAxisBuilder = getLineChart().getValuesAxisBuilder();
        XYChartSeries[] series = getLineChart().getData().getSeries();

        // Rebuild bars for serie values
        List<AxisValue> valuesAxisValues = valuesAxisBuilder.getValues(serie.getValuesAxisProperty());
        List<AxisValue> categoryAxisValues = categoriesAxisBuilder.getValues(getLineChart().getData().getCategoryAxisProperty());
        List<Line> lines = seriesValues.get(serie.getName());
        Double lastX = null;
        Double lastY = null;
        if (categoryAxisValues != null && categoryAxisValues.size() > 0)
        {
            for (int i = 0; i < categoryAxisValues.size(); i++)
            {
                AxisValue categoryAxisvalue = categoryAxisValues.get(i);
                double xAxisValuePosition = categoryAxisvalue.getPosition();
                AxisValue valueAxisvalue = valuesAxisValues.get(i);
                double yAxisValuePosition = valueAxisvalue.getPosition();

                // Calculate positions.
                Double x = null;
                Double y = null;
                if (lastX == null && lastY == null) {
                    lastY = height - yAxisValuePosition;
                    lastX = xAxisValuePosition;
                } else {
                    y = height - yAxisValuePosition;
                    x = xAxisValuePosition;
                }

                /*
                TODO
                 
                // If current bar is not in Y axis intervals (max / min values), resize it and apply an alpha.
                boolean isOutOfChartArea = y < 0;
                if (isOutOfChartArea)
                {
                    alpha = 0.1d;
                    barHeight = height;
                    y = 0;
                }
                */

                if (lastX != null && lastY != null && x != null && y != null) {
                    Point2D p1 = new Point2D(lastX, lastY);
                    Point2D p2 = new Point2D(x, y);
                    final Line line = lines.get(i - 1);
                    line.moveToTop();
                    line.setDraggable(false);

                    doAnimateValues(line, p1, p2, width, height, serie.getColor(), isSeriesNew);
                    
                    lastX = x;
                    lastY = y;
                }
            }
        }

        return this;
    }

    protected AbstractLineChartAnimation calculateValuesForHorizontal(final XYChartSeries serie, final int numSerie, Double width, final Double height, boolean isSeriesNew)
    {
        // TODO
        return this;
    }

    protected abstract void doAnimateValues(final Line line, final Point2D p1, final Point2D p2, final Double w, final Double h, IColor color, final boolean isSeriesNew);
}