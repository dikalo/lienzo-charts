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

import java.util.List;
import java.util.Map;

import com.ait.lienzo.charts.client.core.animation.AbstractChartAnimation;
import com.ait.lienzo.charts.client.core.legend.ChartLegend;
import com.ait.lienzo.charts.client.core.xy.axis.AxisBuilder;
import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.charts.client.core.xy.bar.BarChart;
import com.ait.lienzo.charts.client.core.xy.bar.BarChartLabel;
import com.ait.lienzo.charts.client.core.xy.bar.BarChartTooltip;
import com.ait.lienzo.charts.shared.core.types.ChartDirection;
import com.ait.lienzo.charts.shared.core.types.LabelsPosition;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;

/**
 * <p>Clears the Bar Chart children shapes by animating them to positions /size that perform a clear visual effect..</p> 
 */
public class BarChartClearAnimation extends AbstractChartAnimation
{
    public BarChartClearAnimation(final BarChart barChart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(barChart, barChart.getChartWidth(), barChart.getChartHeight(), tweener, duration, callback);
        
        init(barChart.getChartWidth(), barChart.getChartHeight());
    }

    protected BarChart getBarChart()
    {
        return (BarChart) getNode();
    }

    protected boolean isVertical()
    {
        return getBarChart().isVertical();
    }

    private void init(final double chartWidth, final double chartHeight)
    {
        // Title & Legend & tooltip.
        final Text chartTitle = getBarChart().getChartTitle();
        final ChartLegend legend = getBarChart().getChartLegend();
        final BarChartTooltip tooltip = getBarChart().getChartTooltip();
        if (legend != null) legend.removeFromParent();
        if (tooltip != null) tooltip.removeFromParent();

        // Bar children.
        final List<Text> categoriesAxisTitles = getBarChart().getCategoriesAxisTitle();
        final List<Text> valuesAxisTitles = getBarChart().getValuesAxisTitle();
        final List<BarChartLabel> seriesLabels = getBarChart().getSeriesLabels();
        final AxisBuilder categoriesAxisBuilder = getBarChart().getCategoriesAxisBuilder();
        //final List<AxisLabel> labels = categoriesAxisBuilder.getLabels();
        final List<Line> valuesAxisIntervals = getBarChart().getValuesAxisIntervals();
        final List<BarChartLabel> valuesLabels = getBarChart().getValuesLabels();
        final Map<String, List<Rectangle>> seriesValues = getBarChart().getSeriesValues();

        // Title.
        if (chartTitle != null)
        {
            destroyTitle(chartTitle);
        }
        // Categories axis title.
        if (categoriesAxisTitles != null && !categoriesAxisTitles.isEmpty())
        {
            for (Text t : categoriesAxisTitles)
            {
                destroyTitle(t);
            }
        }
        // Values axis title.
        if (valuesAxisTitles != null && !valuesAxisTitles.isEmpty())
        {
            for (Text t : valuesAxisTitles)
            {
                destroyTitle(t);
            }
        }
        // Categories labels.
        if (seriesLabels != null)
        {
            for (BarChartLabel label : seriesLabels)
            {
                destroyCategoriesAxisLabel(label);

            }
        }
        // Values labels.
        if (valuesLabels != null)
        {
            for (BarChartLabel label : valuesLabels)
            {
                destroyValuesAxisLabel(label);

            }
        }
        // Create the nodes' animations.
        final double yClearPos = chartHeight;
        final double xClearPos = ChartDirection.POSITIVE.equals(getBarChart().getDirection()) ? 0 : chartWidth;
        // Apply animation values axis intervals.
        if (valuesAxisIntervals != null)
        {
            for (final Line line : valuesAxisIntervals)
            {
                if (line != null)
                {
                    final double clearDiff = isVertical() ? (yClearPos - line.getPoints().get(1).getY()) : (xClearPos - line.getPoints().get(1).getX());
                    if (isVertical()) add(line, buildAnimationProperties(null, clearDiff));
                    else add(line, buildAnimationProperties(clearDiff, null));
                }
            }
        }
        AnimationProperties animationProperties2 = new AnimationProperties();
        animationProperties2.push(AnimationProperty.Properties.Y(yClearPos));
        animationProperties2.push(AnimationProperty.Properties.HEIGHT(0d));

        // Apply animation to values.
        if (seriesValues != null)
        {
            for (Map.Entry<String, List<Rectangle>> entry : seriesValues.entrySet())
            {
                for (Rectangle rectangle : entry.getValue())
                {
                    if (isVertical()) add(rectangle, buildAnimationProperties(null, yClearPos, null, 0d));
                    else add(rectangle, buildAnimationProperties(null, null, xClearPos, null));
                }
            }
        }
        // Create axis titles' animations.
        if (!getBarChart().getCategoriesAxisTitle().isEmpty()) add(getBarChart().getCategoriesAxisTitle().get(0), buildAnimationProperties(null, null, 0d, 0d));
        if (!getBarChart().getValuesAxisTitle().isEmpty()) add(getBarChart().getValuesAxisTitle().get(0), buildAnimationProperties(null, null, 0d, 0d));
    }

    private void destroyCategoriesAxisLabel(final BarChartLabel chartLabel)
    {
        // Final positions.
        if (isVertical())
        {
            final LabelsPosition valuesAxisLabelPosition = getBarChart().getValuesAxisLabelsPosition();
            final double lx = LabelsPosition.RIGHT.equals(valuesAxisLabelPosition) ? getBarChart().getChartWidth() : 0;
            add(chartLabel, buildAnimationProperties(lx, 0d));
        }
        else
        {
            final LabelsPosition valuesAxisLabelPosition = getBarChart().getValuesAxisLabelsPosition();
            final double ly = LabelsPosition.BOTTOM.equals(valuesAxisLabelPosition) ? getBarChart().getChartHeight() : 0;
            add(chartLabel, buildAnimationProperties(0d, ly));
        }
        // Alpha animation.
        // add(chartLabel, buildAnimationProperties(0d));
    }

    private void destroyValuesAxisLabel(final BarChartLabel chartLabel)
    {
        // Final positions.
        if (isVertical())
        {
            final LabelsPosition categoryAxisLabelPosition = getBarChart().getCategoriesAxisLabelsPosition();
            final double ly = LabelsPosition.BOTTOM.equals(categoryAxisLabelPosition) ? getBarChart().getChartHeight() : 0;
            add(chartLabel, buildAnimationProperties(0d, ly));
        }
        else
        {
            final LabelsPosition categoryAxisLabelPosition = getBarChart().getCategoriesAxisLabelsPosition();
            final double lx = LabelsPosition.RIGHT.equals(categoryAxisLabelPosition) ? getBarChart().getChartWidth() : 0;
            add(chartLabel, buildAnimationProperties(lx, 0d));
        }
        // Alpha animation.
        // add(chartLabel, buildAnimationProperties(0d));
    }

    private void destroyTitle(final Shape<?> shape)
    {
        // Initial alpha.
        shape.setAlpha(1);

        // Animation to final alpha.
        add(shape, buildAnimationProperties(0d));
    }

    @Override
    public IAnimation doClose()
    {
        getBarChart().clear();
        super.doClose();
        return this;
    }
}
