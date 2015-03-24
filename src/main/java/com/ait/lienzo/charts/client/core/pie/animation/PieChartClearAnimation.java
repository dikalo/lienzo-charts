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
import com.ait.lienzo.client.core.animation.*;
import com.ait.lienzo.client.core.shape.Text;

import java.util.List;

import static com.ait.lienzo.charts.client.core.pie.animation.AbstractPieChartAnimation.buildAnimationProperties;

public class PieChartClearAnimation extends StackedTweeningAnimation
{

    public PieChartClearAnimation(final PieChart pieChart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(pieChart, tweener, duration, callback);

        init(pieChart.getChartWidth(), pieChart.getChartHeight());
    }

    protected PieChart getPieChart()
    {
        return (PieChart) getNode();
    }

    private void init(final double chartWidth, final double chartHeight)
    {
        // Create the animation properties.
        final List<Text> texts = getPieChart().getTexts();

        if (texts != null)
        {
            for (Text text : texts)
            {
                // add(line, buildAnimationProperties(null, clearDiff, null, null));
                if (text != null) add(text, buildAnimationProperties(0d, 0d, 0d, null));
            }
        }
        final AnimationProperties animationProperties3 = new AnimationProperties();

        animationProperties3.push(AnimationProperty.Properties.SCALE(0d));

        final List<PieChart.PieSlice> pieSlices = getPieChart().getPieSlices();

        if (pieSlices != null)
        {
            for (PieChart.PieSlice slice : pieSlices)
            {
                if (slice != null) add(slice, animationProperties3);
            }
        }
    }

    @Override
    public IAnimation doClose()
    {
        getPieChart().clear();

        super.doClose();

        return this;
    }
}
