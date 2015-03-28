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
import com.ait.lienzo.charts.client.core.animation.PointsAnimationProperty;
import com.ait.lienzo.charts.client.core.xy.XYChart;
import com.ait.lienzo.charts.client.core.xy.axis.AxisBuilder;
import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.charts.client.core.xy.label.XYChartLabel;
import com.ait.lienzo.charts.client.core.xy.label.XYChartLabelFormatter;
import com.ait.lienzo.charts.shared.core.types.LabelsPosition;
import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;

import java.util.List;

public abstract class XYChartAnimation extends AbstractChartAnimation
{
    public XYChartAnimation(final XYChart chart, final double chartWidth, final double chartHeight, AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(chart, chartWidth, chartHeight, tweener, duration, callback);
    }

    protected XYChart getXYChart()
    {
        return (XYChart) getNode();
    }

    protected boolean isVertical()
    {
        return getXYChart().isVertical();
    }

    protected void calculate(final double w, final double h)
    {
        super.calculate(w, h);

        calculateCategoriesAxisTitle(w, h);
        calculateValuesAxisTitle(w, h);
        calculateCategoriesAxisIntervals(w, h);
        calculateValuesAxisIntervals(w, h);
        calculateValues(w, h);
        
    }

    @Override
    protected void doAnimateChartTitle(Text chartTitle, Double x, Double y)
    {
        createTitle(chartTitle, x, y);
    }
    
    protected abstract void calculateValues(final double w, final double h);

    protected void calculateCategoriesAxisTitle(final double w, final double h)
    {
        // Category axis title.
        if (!getXYChart().getCategoriesAxisTitle().isEmpty())
        {
            final Double ctx = isVertical() ? w / 2 : null;
            final Double cty = isVertical() ? 30d : h / 2;
            doAnimateCategoriesAxisTitle((Text) getXYChart().getCategoriesAxisTitle().get(0), ctx, cty);
        }
    }

    protected void doAnimateCategoriesAxisTitle(final Text categoriesAxisTitle, final Double x, final Double y)
    {
        createTitle(categoriesAxisTitle, x, y);
    }

    protected void calculateValuesAxisTitle(final double w, final double h)
    {
        // Values axis title.
        if (!getXYChart().getValuesAxisTitle().isEmpty())
        {
            final Double vtx = isVertical() ? null : w / 2;
            final Double vty = isVertical() ? h / 2 : 30d;
            doAnimateValuesAxisTitle((Text) getXYChart().getValuesAxisTitle().get(0), vtx, vty);
        }
    }

    protected void doAnimateValuesAxisTitle(final Text valuesAxisTitle, final Double x, final Double y)
    {
        createTitle(valuesAxisTitle, x, y);
    }

    protected void calculateCategoriesAxisIntervals(final double w, final double h)
    {
        if (getXYChart().isShowCategoriesLabels())
        {
            final double ml = getXYChart().getMarginLeft();
            final double mr = getXYChart().getMarginRight();
            final double mt = getXYChart().getMarginTop();
            final double mb = getXYChart().getMarginBottom();
            final List<XYChartLabel> seriesLabels = getXYChart().getSeriesLabels();
            final AxisBuilder categoriesAxisBuilder = getXYChart().getCategoriesAxisBuilder();
            final List<AxisLabel> labels = categoriesAxisBuilder.getCategoriesAxisLabels();
            final int lsize = labels.size();
            final LabelsPosition clp = getXYChart().getCategoriesAxisLabelsPosition();

            if (labels != null && !labels.isEmpty())
            {
                // Check max labels size.
                final double maxLabelWidth = isVertical() ? w / lsize : (clp.equals(LabelsPosition.RIGHT) ? mr : ml);
                final double maxLabelHeight = isVertical() ? (clp.equals(LabelsPosition.BOTTOM) ? mb : mt) : h / lsize;
                final XYChartLabelFormatter categoriesLabelFormatter = new XYChartLabelFormatter(seriesLabels);

                // Apply format to the labels.
                categoriesLabelFormatter.format(maxLabelWidth, maxLabelHeight);

                for (int i = 0; i < labels.size(); i++)
                {
                    final AxisLabel label = labels.get(i);
                    final double position = label.getPosition();
                    final XYChartLabel chartLabel = seriesLabels.get(i);

                    if (isVertical())
                    {
                        final double labelWidth = chartLabel.getLabelWidth();
                        //TODO: Use ComposedTweeningAnimation#add()
                        doAnimateCategoriesAxisIntervals(chartLabel, label, position - labelWidth / 2, 10d);
                    }
                    else
                    {
                        double xPos = 0;
                        if (!clp.equals(LabelsPosition.RIGHT))
                        {
                            // Left.
                            final double margin = ml;
                            final double lw = chartLabel.getLabelWidth();
                            xPos = (lw + 5 > margin) ? 0 : margin - lw - 5;
                        }
                        else
                        {
                            // Right.
                            xPos = 5;
                        }
                        //TODO: Use ComposedTweeningAnimation#add()
                        doAnimateCategoriesAxisIntervals(chartLabel, label, xPos, position - chartLabel.getLabelHeight() / 2);
                    }
                }
            }
            else
            {
                seriesLabels.clear();
            }
        }
    }

    protected void doAnimateCategoriesAxisIntervals(final XYChartLabel chartLabel, final AxisLabel axisLabel, final Double x, final Double y)
    {
        // Init label position.
        createCategoriesAxisLabel(chartLabel);

        // Text.
        chartLabel.set(axisLabel);

        // Add animation to final position.
        add(chartLabel, buildAnimationProperties(x, y));
    }

    private void createCategoriesAxisLabel(final XYChartLabel chartLabel) {

        // Initial position.
        if (isVertical())
        {
            final LabelsPosition valuesAxisLabelPosition = getXYChart().getValuesAxisLabelsPosition();
            final double lx = LabelsPosition.RIGHT.equals(valuesAxisLabelPosition) ? getXYChart().getChartWidth() : 0;
            chartLabel.setX(lx).setY(0);
        }
        else
        {
            final LabelsPosition valuesAxisLabelPosition = getXYChart().getValuesAxisLabelsPosition();
            final double ly = LabelsPosition.BOTTOM.equals(valuesAxisLabelPosition) ? getXYChart().getChartHeight() : 0;
            chartLabel.setX(0).setY(ly);
        }

    }

    protected void calculateValuesAxisIntervals(final double w, final double h)
    {
        final double ml = getXYChart().getMarginLeft();
        final double mr = getXYChart().getMarginRight();
        final double mt = getXYChart().getMarginTop();
        final double mb = getXYChart().getMarginBottom();
        final List<Line> valuesAxisIntervals = getXYChart().getValuesAxisIntervals();
        final List<XYChartLabel> valuesLabels = getXYChart().getValuesLabels();
        final AxisBuilder valuesAxisBuilder = getXYChart().getValuesAxisBuilder();

        final List<AxisLabel> labels = valuesAxisBuilder.getValuesAxisLabels();
        if (labels != null && !labels.isEmpty())
        {
            final int lsize = labels.size();
            final LabelsPosition vlp = getXYChart().getValuesAxisLabelsPosition();

            final double maxLabelWidth = isVertical() ? (vlp.equals(LabelsPosition.LEFT) ? ml : mr) : w / 2;
            final double maxLabelHeight = isVertical() ? h / lsize : (vlp.equals(LabelsPosition.TOP) ? mt : mb);
            final XYChartLabelFormatter valuesLabelFormatter = new XYChartLabelFormatter(valuesLabels);

            // Apply format to the labels.
            valuesLabelFormatter.format(maxLabelWidth, maxLabelHeight);

            for (int i = 0; i < labels.size(); i++)
            {
                final AxisLabel label = labels.get(i);
                final double position = label.getPosition();

                final double p00 = isVertical() ? 0 : position;
                final double p01 = isVertical() ? position : 0;
                final double p10 = isVertical() ? w : position;
                final double p11 = isVertical() ? position : h;
                doAnimateValuesAxisIntervals(valuesAxisIntervals.get(i), p00, p01, p10, p11);

                if (getXYChart().isShowValuesLabels())
                {
                    XYChartLabel chartLabel = valuesLabels.get(i);

                    if (isVertical())
                    {
                        double xPos = 0;
                        if (vlp.equals(LabelsPosition.LEFT))
                        {
                            // Left.
                            final double marginLeft = ml;
                            final double lw = chartLabel.getLabelWidth();
                            xPos = (lw + 5 > marginLeft) ? 0 : marginLeft - lw - 5;
                        }
                        else
                        {
                            // Right.
                            xPos = 5;
                        }
                        doAnimateValuesAxisIntervals(chartLabel, label, xPos, position - 5);
                    }
                    else
                    {
                        double yPos = 0;
                        if (vlp.equals(LabelsPosition.TOP))
                        {
                            // Top.
                            final double marginTop = mt;
                            final double lh = chartLabel.getLabelHeight();
                            yPos = (lh + 5 > marginTop) ? 0 : marginTop - lh - 5;
                        }
                        else
                        {
                            // Bottom.
                            yPos = 5;
                        }
                        doAnimateValuesAxisIntervals(chartLabel, label, position, yPos);
                    }

                }
            }
        }
    }


    protected void doAnimateValuesAxisIntervals(final Line valueAxisInterval, final double p00, final double p01, final double p10, final double p11)
    {
        final AnimationProperties animationProperties = new AnimationProperties();
        animationProperties.push(new PointsAnimationProperty(new Point2D(p00, p01), new Point2D(p10, p11), Attribute.POINTS));
        add(valueAxisInterval, animationProperties);
    }

    protected void doAnimateValuesAxisIntervals(final XYChartLabel chartLabel, final AxisLabel axisLabel, final Double x, final Double y)
    {
        // Init label position.
        createValuesAxisLabel(chartLabel);

        // Text.
        chartLabel.set(axisLabel);

        // Add animation to final position.
        add(chartLabel, buildAnimationProperties(x, y));
    }

    private void createValuesAxisLabel(final XYChartLabel chartLabel) {

        // Initial position.
        if (isVertical())
        {
            final LabelsPosition categoryAxisLabelPosition = getXYChart().getCategoriesAxisLabelsPosition();
            final double ly = LabelsPosition.BOTTOM.equals(categoryAxisLabelPosition) ? getXYChart().getChartHeight() : 0;
            chartLabel.setX(0).setY(ly);
        }
        else
        {
            final LabelsPosition categoryAxisLabelPosition = getXYChart().getCategoriesAxisLabelsPosition();
            final double lx = LabelsPosition.RIGHT.equals(categoryAxisLabelPosition) ? getXYChart().getChartWidth() : 0;
            chartLabel.setX(lx).setY(0);
        }

    }

    private void createTitle(final Shape<?> shape, final Double x, final Double y) {
        // Position.
        if (x != null) shape.setX(x);
        if (y != null) shape.setY(y);

        // Initial alpha.
        shape.setAlpha(0d);

        // Animation to final alpha.
        add(shape, buildAnimationProperties(1d));

    }
}