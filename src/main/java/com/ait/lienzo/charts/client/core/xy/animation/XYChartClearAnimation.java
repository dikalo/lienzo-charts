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

package com.ait.lienzo.charts.client.core.xy.animation;

import com.ait.lienzo.charts.client.core.animation.AbstractChartAnimation;
import com.ait.lienzo.charts.client.core.legend.ChartLegend;
import com.ait.lienzo.charts.client.core.xy.XYChart;
import com.ait.lienzo.charts.client.core.xy.label.XYChartLabel;
import com.ait.lienzo.charts.shared.core.types.ChartDirection;
import com.ait.lienzo.charts.shared.core.types.LabelsPosition;
import com.ait.lienzo.client.core.animation.*;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.ToolTip;

import java.util.List;

public abstract class XYChartClearAnimation extends AbstractChartAnimation
{
    public XYChartClearAnimation(final XYChart<?> chart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(chart, chart.getChartWidth(), chart.getChartHeight(), tweener, duration, callback);

        init(chart.getChartWidth(), chart.getChartHeight());
    }

    protected XYChart<?> getXYChart()
    {
        return (XYChart<?>) getNode();
    }

    protected boolean isVertical()
    {
        return getXYChart().isVertical();
    }

    protected abstract void clearValues(final double chartWidth, final double chartHeight);

    protected void init(final double chartWidth, final double chartHeight)
    {
        // Title & Legend & tooltip.
        final Text chartTitle = getXYChart().getChartTitle();
        final ChartLegend legend = getXYChart().getChartLegend();
        final ToolTip tooltip = getXYChart().getChartTooltip();
        if (legend != null) legend.removeFromParent();
        if (tooltip != null) tooltip.removeFromParent();

        // Bar children.
        final List<Text> categoriesAxisTitles = getXYChart().getCategoriesAxisTitle();
        final List<Text> valuesAxisTitles = getXYChart().getValuesAxisTitle();
        final List<XYChartLabel> seriesLabels = getXYChart().getSeriesLabels();
        final List<Line> valuesAxisIntervals = getXYChart().getValuesAxisIntervals();
        final List<XYChartLabel> valuesLabels = getXYChart().getValuesLabels();

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
            for (XYChartLabel label : seriesLabels)
            {
                destroyCategoriesAxisLabel(label);

            }
        }
        // Values labels.
        if (valuesLabels != null)
        {
            for (XYChartLabel label : valuesLabels)
            {
                destroyValuesAxisLabel(label);

            }
        }
        // Create the nodes' animations.
        final double yClearPos = chartHeight;
        final double xClearPos = ChartDirection.POSITIVE.equals(getXYChart().getDirection()) ? 0 : chartWidth;
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
        clearValues(chartWidth, chartHeight);

        // Create axis titles' animations.
        if (!getXYChart().getCategoriesAxisTitle().isEmpty()) add((Node<?>) getXYChart().getCategoriesAxisTitle().get(0), buildAnimationProperties(null, null, 0d, 0d));
        if (!getXYChart().getValuesAxisTitle().isEmpty()) add((Node<?>) getXYChart().getValuesAxisTitle().get(0), buildAnimationProperties(null, null, 0d, 0d));
    }

    protected void destroyCategoriesAxisLabel(final XYChartLabel chartLabel)
    {
        // Final positions.
        if (isVertical())
        {
            final LabelsPosition valuesAxisLabelPosition = getXYChart().getValuesAxisLabelsPosition();
            final double lx = LabelsPosition.RIGHT.equals(valuesAxisLabelPosition) ? getXYChart().getChartWidth() : 0;
            add(chartLabel, buildAnimationProperties(lx, 0d));
        }
        else
        {
            final LabelsPosition valuesAxisLabelPosition = getXYChart().getValuesAxisLabelsPosition();
            final double ly = LabelsPosition.BOTTOM.equals(valuesAxisLabelPosition) ? getXYChart().getChartHeight() : 0;
            add(chartLabel, buildAnimationProperties(0d, ly));
        }
        // Alpha animation.
        // add(chartLabel, buildAnimationProperties(0d));
    }

    protected void destroyValuesAxisLabel(final XYChartLabel chartLabel)
    {
        // Final positions.
        if (isVertical())
        {
            final LabelsPosition categoryAxisLabelPosition = getXYChart().getCategoriesAxisLabelsPosition();
            final double ly = LabelsPosition.BOTTOM.equals(categoryAxisLabelPosition) ? getXYChart().getChartHeight() : 0;
            add(chartLabel, buildAnimationProperties(0d, ly));
        }
        else
        {
            final LabelsPosition categoryAxisLabelPosition = getXYChart().getCategoriesAxisLabelsPosition();
            final double lx = LabelsPosition.RIGHT.equals(categoryAxisLabelPosition) ? getXYChart().getChartWidth() : 0;
            add(chartLabel, buildAnimationProperties(lx, 0d));
        }
        // Alpha animation.
        // add(chartLabel, buildAnimationProperties(0d));
    }

    protected void destroyTitle(final Shape<?> shape)
    {
        // Initial alpha.
        shape.setAlpha(1);

        // Animation to final alpha.
        add(shape, buildAnimationProperties(0d));
    }

    @Override
    public IAnimation doClose()
    {
        getXYChart().clear();
        super.doClose();
        return this;
    }
}
