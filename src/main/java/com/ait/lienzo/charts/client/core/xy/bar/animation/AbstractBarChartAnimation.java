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

import com.ait.lienzo.charts.client.core.legend.ChartLegend;
import com.ait.lienzo.charts.client.core.xy.animation.XYChartAnimation;
import com.ait.lienzo.charts.client.core.xy.XYChartSeries;
import com.ait.lienzo.charts.client.core.xy.axis.AxisBuilder;
import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.charts.client.core.xy.axis.AxisValue;
import com.ait.lienzo.charts.client.core.xy.bar.BarChart;
import com.ait.lienzo.charts.shared.core.types.ChartDirection;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.IColor;

import java.util.List;
import java.util.Map;

public abstract class AbstractBarChartAnimation extends XYChartAnimation
{
    public AbstractBarChartAnimation(final BarChart barChart, final double chartWidth, final double chartHeight, AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(barChart, chartWidth, chartHeight, tweener, duration, callback);
    }

    protected BarChart getBarChart()
    {
        return (BarChart) getNode();
    }

    protected void calculateValues(final double w, final double h)
    {
        final AxisBuilder<?> categoriesAxisBuilder = getBarChart().getCategoriesAxisBuilder();
        final XYChartSeries[] series = getBarChart().getData().getSeries();
        final ChartLegend legend = getBarChart().getChartLegend();

        // Find removed series in order to remove bar rectangle instances.
        for (String removedSeriesName : categoriesAxisBuilder.getDataSummary().getRemovedSeries())
        {
            // TODO: Remove series values should be done only in a BarChartReloadAnimation?
            getBarChart().removeSeriesValues(removedSeriesName);
            if (legend != null) legend.remove(removedSeriesName).build();
        }
        categoriesAxisBuilder.getDataSummary().getRemovedSeries().clear();

        // Iterate over all series.
        for (int numSeries = 0; numSeries < series.length; numSeries++)
        {
            final XYChartSeries _series = series[numSeries];
            if (_series != null)
            {
                // If a new serie is added, draw new bar rectangle instances.
                boolean isSeriesNew = false;
                if (categoriesAxisBuilder.getDataSummary().getAddedSeries().contains(_series.getName()))
                {
                    // TODO: Build series values should be done only in a BarChartReloadAnimation?
                    getBarChart().buildSeriesValues(_series, numSeries);
                    if (legend != null) legend.add(new ChartLegend.ChartLegendEntry(_series.getName(), _series.getColor())).build();
                    categoriesAxisBuilder.getDataSummary().getAddedSeries().remove(_series.getName());
                    isSeriesNew = true;
                }
                if (isVertical()) calculateValuesForVertical(_series, numSeries, w, h, isSeriesNew);
                else calculateValuesForHorizontal(_series, numSeries, w, h, isSeriesNew);
            }
        }
    }

    protected AbstractBarChartAnimation calculateValuesForVertical(final XYChartSeries serie, final int numSerie, final Double width, final Double height, boolean isSeriesNew)
    {
        final Map<String, List<Rectangle>> seriesValues = getBarChart().getSeriesValues();
        final AxisBuilder<?> categoriesAxisBuilder = getBarChart().getCategoriesAxisBuilder();
        final AxisBuilder<?> valuesAxisBuilder = getBarChart().getValuesAxisBuilder();
        final XYChartSeries[] series = getBarChart().getData().getSeries();

        // Rebuild bars for series values
        final List<? extends  AxisValue<?>> valuesAxisValues = valuesAxisBuilder.getValues(serie.getValuesAxisProperty());
        final List<? extends AxisValue<?>> categoryAxisValues = categoriesAxisBuilder.getValues(getBarChart().getData().getCategoryAxisProperty());
        final List<Rectangle> bars = seriesValues.get(serie.getName());

        if (categoryAxisValues != null && categoryAxisValues.size() > 0)
        {
            for (int i = 0; i < categoryAxisValues.size(); i++)
            {
                final AxisValue<?> categoryAxisValue = categoryAxisValues.get(i);
                final AxisValue<?> valueAxisValue = valuesAxisValues.get(i);
                final double yAxisValuePosition = valueAxisValue.getPosition();

                // Obtain width and height values for the bar.
                double barHeight = yAxisValuePosition;
                double barWidth = getWithForBar(width, series.length, categoryAxisValues.size());
                if (barWidth <= 0) barWidth = 1;

                // Calculate bar positions.
                double y = getBarChart().getDirection().equals(ChartDirection.POSITIVE) ? height - barHeight : height;
                double x = categoryAxisValue.getPosition() - barWidth/2 + (barWidth * numSerie);
                double alpha = 1d;

                // If current bar is not in Y axis intervals (max / min values), resize it and apply an alpha.
                boolean isOutOfChartArea = y < 0;
                if (isOutOfChartArea)
                {
                    alpha = 0.1d;
                    barHeight = height;
                    y = 0;
                }

                // Obtain the shape instance, add mouse handlers and reposition/resize it.
                final Rectangle barObject = bars.get(i);
                barObject.moveToTop();
                barObject.setDraggable(true);

                double lastBarWidth = barWidth - BarChart.BAR_SEPARATION;
                if (lastBarWidth > BarChart.BAR_MAX_SIZE)
                {
                    lastBarWidth -= barWidth * BarChart.BAR_MAX_SIZE_PROPORTION;
                    x += (barWidth / 2 - lastBarWidth / 2);
                }
                doAnimateValues(barObject, x, y, lastBarWidth, barHeight, serie.getColor(), alpha, isSeriesNew);
            }
        }

        return this;
    }

    protected double getWithForBar(final double chartWidth, final int numSeries, final int valuesCount)
    {
        // If exist more than one serie, and no stacked attribute is set, split each serie bar into the series count value.
        return getAvailableWidth(chartWidth, valuesCount) / valuesCount / numSeries;
    }

    protected double getAvailableWidth(final double chartWidth, final int valuesCount)
    {
        final int yAxisDivisions = getBarChart().getValuesAxis().getSegments();
        return chartWidth - (yAxisDivisions * (valuesCount + 1));
    }

    protected AbstractBarChartAnimation calculateValuesForHorizontal(final XYChartSeries serie, final int numSerie, Double width, final Double height, boolean isSeriesNew)
    {
        final Map<String, List<Rectangle>> seriesValues = getBarChart().getSeriesValues();
        final AxisBuilder<?> categoriesAxisBuilder = getBarChart().getCategoriesAxisBuilder();
        final AxisBuilder<?> valuesAxisBuilder = getBarChart().getValuesAxisBuilder();
        final XYChartSeries[] series = getBarChart().getData().getSeries();

        // Rebuild bars for series values
        final List<? extends AxisValue<?>> yAxisValues = categoriesAxisBuilder.getValues(getBarChart().getData().getCategoryAxisProperty());
        final List<? extends AxisValue<?>> xAxisValues = valuesAxisBuilder.getValues(serie.getValuesAxisProperty());
        final List<AxisLabel> xAxisLabels = valuesAxisBuilder.getValuesAxisLabels();
        final List<Rectangle> bars = seriesValues.get(serie.getName());

        if (yAxisValues != null && yAxisValues.size() > 0)
        {
            for (int i = 0; i < yAxisValues.size(); i++)
            {
                final AxisValue<?> xAxisValue = xAxisValues.get(i);
                final AxisValue<?> yAxisValue = yAxisValues.get(i);
                final double xAxisValuePosition = xAxisValue.getPosition();

                // Obtain width and height values for the bar.
                final int valuesSize = yAxisValues.size();
                double barWidth = xAxisValuePosition;
                double barHeight = getHeightForBar(height, series.length, valuesSize);
                if (barHeight <= 0) barHeight = 1;

                // Calculate bar positions.
                double x = getBarChart().getDirection().equals(ChartDirection.POSITIVE) ? 0d : width - barWidth;
                double y = yAxisValue.getPosition() - barHeight/2 + (barHeight * numSerie);
                double alpha = 1d;

                // If current bar is not in Y axis intervals (max / min values), resize it and apply an alpha.
                double lastXIntervalPosition = xAxisLabels.get(xAxisLabels.size() - 1).getPosition();
                boolean isOutOfChartArea = barWidth > lastXIntervalPosition;
                if (isOutOfChartArea)
                {
                    alpha = 0.1d;
                    barWidth = width;
                }

                // Obtain the shape instance, add mouse handlers and reposition/resize it.
                final Rectangle barObject = bars.get(i);
                barObject.setDraggable(true);

                double lastBarHeight = barHeight - BarChart.BAR_SEPARATION;
                if (lastBarHeight > BarChart.BAR_MAX_SIZE)
                {
                    lastBarHeight -= barHeight * BarChart.BAR_MAX_SIZE_PROPORTION;
                    y += (barHeight / 2 - lastBarHeight / 2);
                }
                doAnimateValues(barObject, x, y, barWidth, lastBarHeight, serie.getColor(), alpha, isSeriesNew);
            }
        }
        return this;
    }

    protected double getHeightForBar(final double chartHeight, final int numSeries, final int valuesCount)
    {
        // If exist more than one serie, and no stacked attribute is set, split each serie bar into the series count value.
        return chartHeight / valuesCount / numSeries;
    }

    protected abstract void doAnimateValues(final Rectangle bar, final Double x, final Double y, final Double w, final Double h, IColor color, final Double alpha, final boolean isSeriesNew);
}