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

import com.ait.lienzo.charts.client.core.xy.animation.XYChartClearAnimation;
import com.ait.lienzo.charts.client.core.xy.bar.BarChart;
import com.ait.lienzo.charts.shared.core.types.ChartDirection;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Rectangle;

import java.util.List;
import java.util.Map;

/**
 * <p>Clears the Bar Chart children shapes by animating them to positions /size that perform a clear visual effect..</p> 
 */
public class BarChartClearAnimation extends XYChartClearAnimation
{
    public BarChartClearAnimation(final BarChart barChart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(barChart, tweener, duration, callback);
        
        init(barChart.getChartWidth(), barChart.getChartHeight());
    }

    protected BarChart getBarChart()
    {
        return (BarChart) getNode();
    }

    @Override
    protected void clearValues(double chartWidth, double chartHeight) {
        final Map<String, List<Rectangle>> seriesValues = getBarChart().getSeriesValues();
        final double yClearPos = chartHeight;
        final double xClearPos = ChartDirection.POSITIVE.equals(getBarChart().getDirection()) ? 0 : chartWidth;
        
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
    }

}
