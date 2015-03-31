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

import com.ait.lienzo.charts.client.core.animation.StackedTweeningAnimation;
import com.ait.lienzo.charts.client.core.pie.PieChart;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.ToolTip;

public class PieChartResizeAnimation extends AbstractPieChartAnimation
{
    public PieChartResizeAnimation(PieChart pieChart, double chartWidth, double chartHeight, AnimationTweener tweener, double duration, IAnimationCallback callback)
    {
        super(pieChart, chartWidth, chartHeight, tweener, duration, callback);

        add(pieChart, buildAnimationProperties(null, null, chartWidth, chartHeight));
    }

    @Override
    protected boolean apply(final StackedTweeningAnimation.NodeAnimation nodeAnimation, final double percent)
    {
        boolean apply = super.apply(nodeAnimation, percent);

        final double w = getPieChart().getChartWidth();

        final double h = getPieChart().getChartHeight();

        calculate(w, h);

        return apply;
    }

    @Override
    protected void doAnimateToolTip(ToolTip tooltip, double x, double y)
    {
        tooltip.show(x, y);
    }

    @Override
    protected void doAnimatePieSlice(PieChart.PieSlice slice, double radius, double startAngle, double endAngle)
    {
        setShapeCircularAttributes(slice, radius, startAngle, endAngle);
    }

    @Override
    protected void doAnimateText(Text text, double x, double y, double alpha)
    {
        setShapeAttributes(text, x, y, alpha);
    }
}
