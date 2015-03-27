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

import com.ait.lienzo.charts.client.core.AbstractChart;
import com.ait.lienzo.charts.client.core.animation.PointsAnimationProperty;
import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.charts.client.core.xy.bar.BarChart;
import com.ait.lienzo.charts.client.core.xy.bar.animation.AbstractBarChartAnimation;
import com.ait.lienzo.charts.client.core.xy.label.XYChartLabel;
import com.ait.lienzo.charts.client.core.xy.line.LineChart;
import com.ait.lienzo.charts.shared.core.types.LabelsPosition;
import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.IColor;
import com.google.gwt.core.client.GWT;

/**
 * <p>Creates the Line Chart children shapes by setting initial positions/sizes and adding animations to target positions/sizes.</p> 
 */
public class LineChartCreateAnimation extends AbstractLineChartAnimation
{
    
    public LineChartCreateAnimation(final LineChart lineChart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(lineChart, lineChart.getChartWidth(), lineChart.getChartHeight(), tweener, duration, callback);
        add(lineChart, buildAnimationProperties(null, null, lineChart.getChartWidth(), lineChart.getChartHeight()));
    }

    @Override
    protected void init(final AbstractChart<?> node) {
        super.init(node);

        // Build chart children.
        node.draw();

        calculate(m_width, m_height);
    }

    @Override
    protected void doAnimateValues(Line line, Point2D p1, Point2D p2, Double w, Double h, IColor color, boolean isSeriesNew) {
        // Initial bar positions.
        /* 
        TODO
         
        final double initialX = isVertical() ? x : 0d;
        final double initialY = isVertical() ? m_height : y;
        bar.setX(initialX);
        bar.setY(initialY);
        */


        final AnimationProperties animationProperties = new AnimationProperties();
        animationProperties.push(AnimationProperty.Properties.FILL_COLOR(color));
        animationProperties.push(new PointsAnimationProperty(p1, p2, Attribute.POINTS));
        add(line, animationProperties);

        GWT.log("doAnimateValues - Added line");
    }
}
