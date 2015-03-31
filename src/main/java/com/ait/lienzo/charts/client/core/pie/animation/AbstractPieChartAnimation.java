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

package com.ait.lienzo.charts.client.core.pie.animation;

import com.ait.lienzo.charts.client.core.animation.AbstractChartAnimation;
import com.ait.lienzo.charts.client.core.model.DataTable;
import com.ait.lienzo.charts.client.core.model.PieChartData;
import com.ait.lienzo.charts.client.core.pie.PieChart;
import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.ToolTip;

public abstract class AbstractPieChartAnimation extends AbstractChartAnimation
{
    public AbstractPieChartAnimation(final PieChart pieChart, final double chartWidth, final double chartHeight, AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(pieChart, chartWidth, chartHeight, tweener, duration, callback);
    }

    protected PieChart getPieChart()
    {
        return (PieChart) getNode();
    }

    protected void calculate(final double w, final double h)
    {
        double radius = getPieChart().getRadius(w, h);

        PieChartData data = getPieChart().getData();

        if (radius <= 0 || (null == data) || (data.size() < 1))
        {
            return;
        }
        super.calculate(w, h);

        moveGroups(w, h);

        final DataTable dataTable = data.getDataTable();
        //final String[] categories = dataTable.getColumn(data.getCategoriesProperty()).getStringValues();
        final Double[] values = dataTable.getColumn(data.getValuesProperty()).getNumericValues();

        double sofar = 0;

        double total = 0;

        for (int i = 0; i < values.length; i++)
        {
            total += values[i];
        }
        getPieChart().getLabels().setListening(false);

        for (int i = 0; i < values.length; i++)
        {
            final double value = values[i] / total;

            final PieChart.PieSlice slice = getPieChart().getPieSlices().get(i);

            if (slice != null)
            {
                double startAngle = PieChart.PieSlice.buildStartAngle(sofar);

                double endAngle = PieChart.PieSlice.buildEngAngle(sofar, value);

                doAnimatePieSlice(slice, radius, startAngle, endAngle);
            }
            else
            {
                // TODO: New data values added.
            }
            double s_ang = Math.PI * (2.0 * sofar);

            double e_ang = Math.PI * (2.0 * (sofar + value));

            double n_ang = (s_ang + e_ang) / 2.0;

            if (n_ang > (Math.PI * 2.0))
            {
                n_ang = n_ang - (Math.PI * 2.0);
            }
            else if (n_ang < 0)
            {
                n_ang = n_ang + (Math.PI * 2.0);
            }
            double lx = Math.sin(n_ang) * (radius / 2);

            double ly = 0 - Math.cos(n_ang) * (radius / 2);

            //TextAlign align;

            if (n_ang <= (Math.PI * 0.5))
            {
                lx += 2;

                //align = TextAlign.LEFT;
            }
            else if ((n_ang > (Math.PI * 0.5)) && (n_ang <= Math.PI))
            {
                lx += 2;

                //align = TextAlign.LEFT;
            }
            else if ((n_ang > Math.PI) && (n_ang <= (Math.PI * 1.5)))
            {
                lx -= 2;

                //align = TextAlign.RIGHT;
            }
            else
            {
                lx -= 2;

                //align = TextAlign.RIGHT;
            }
            final double xToolTip = lx;
            final double yToolTip = ly;
            slice.addNodeMouseEnterHandler(new NodeMouseEnterHandler()
            {
                @Override
                public void onNodeMouseEnter(NodeMouseEnterEvent event)
                {
                    // Position the tooltip.
                    doAnimateToolTip(getPieChart().getTooltip(), xToolTip + w / 2, yToolTip + h / 2);
                }
            });
            Text text = getPieChart().getTexts().get(i);

            if (text != null)
            {
                double textWidth = text.getBoundingBox().getWidth();
                double textHeight = text.getBoundingBox().getHeight();
                doAnimateText(text, lx - textWidth / 2, ly - textHeight / 2, 1d);
            }
            else
            {
                // TODO: New data values added.
            }
            sofar += value;
        }
        getPieChart().getLabels().moveToTop();
    }

    private void moveGroups(double w, double h)
    {
        final Group labels = getPieChart().getLabels();
        final Group slices = getPieChart().getSlices();
        final double x = w / 2;
        final double y = h / 2;
        labels.setX(x).setY(y);
        slices.setX(x).setY(y);
    }

    protected abstract void doAnimateToolTip(final ToolTip tooltip, final double x, final double y);

    protected abstract void doAnimatePieSlice(final PieChart.PieSlice slice, final double radius, final double startAngle, final double endAngle);

    protected abstract void doAnimateText(final Text text, final double x, final double y, final double alpha);

    protected static AnimationProperties buildAnimationProperties(Double x, Double y, Double w, Double h)
    {
        if (x == null && y == null && w == null && h == null) return null;
        AnimationProperties animationProperties = new AnimationProperties();
        if (x != null) animationProperties.push(AnimationProperty.Properties.X(x));
        if (y != null) animationProperties.push(AnimationProperty.Properties.Y(y));
        if (w != null) animationProperties.push(AnimationProperty.Properties.WIDTH(w));
        if (h != null) animationProperties.push(AnimationProperty.Properties.HEIGHT(h));
        return animationProperties;
    }

    protected void setShapeAttributes(final Shape<?> shape, final Double x, final Double y, final Double alpha)
    {
        if (shape != null)
        {
            if (x != null) shape.setX(x);
            if (y != null) shape.setY(y);
            if (alpha != null) shape.setAlpha(alpha);
        }
    }

    protected void setShapeCircularAttributes(final Shape<?> shape, final Double radius, final Double startAngle, final Double endAngle)
    {
        if (shape != null)
        {
            if (radius != null) shape.getAttributes().put(Attribute.RADIUS.getProperty(), radius);
            if (startAngle != null) shape.getAttributes().put(Attribute.START_ANGLE.getProperty(), startAngle);
            if (endAngle != null) shape.getAttributes().put(Attribute.END_ANGLE.getProperty(), endAngle);
        }
    }
}