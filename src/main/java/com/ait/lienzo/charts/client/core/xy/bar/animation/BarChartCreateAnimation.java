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

package com.ait.lienzo.charts.client.core.xy.bar.animation;

import com.ait.lienzo.charts.client.core.AbstractChart;
import com.ait.lienzo.charts.client.core.xy.bar.BarChart;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.IColor;

/**
 * <p>Creates the Bar Chart children shapes by setting initial positions/sizes and adding animations to target positions/sizes.</p> 
 */
public class BarChartCreateAnimation extends AbstractBarChartAnimation
{
    
    public BarChartCreateAnimation(final BarChart barChart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(barChart, barChart.getChartWidth(), barChart.getChartHeight(), tweener, duration, callback);
        add(barChart, buildAnimationProperties(null, null, barChart.getChartWidth(), barChart.getChartHeight()));
    }

    @Override
    protected void init(final AbstractChart<?> node) {
        super.init(node);

        // Build chart children.
        node.draw();

        calculate(m_width, m_height);
    }

    @Override
    protected void doAnimateValues(final Rectangle bar, final Double x, final Double y, final Double w, final Double h, final IColor color, final Double alpha, final boolean isSeriesNew)
    {
        // Initial bar positions.
        final double initialX = isVertical() ? x : 0d;
        final double initialY = isVertical() ? m_height : y;
        bar.setX(initialX);
        bar.setY(initialY);

        final AnimationProperties animationProperties = buildAnimationProperties(x, y, w, h);
        animationProperties.push(AnimationProperty.Properties.FILL_COLOR(color));
        animationProperties.push(AnimationProperty.Properties.ALPHA(alpha));
        add(bar, animationProperties);
    }
    
}
