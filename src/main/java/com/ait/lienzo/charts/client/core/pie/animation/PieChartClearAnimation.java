
package com.ait.lienzo.charts.client.core.pie.animation;

import com.ait.lienzo.charts.client.core.animation.StackedTweeningAnimation;
import com.ait.lienzo.charts.client.core.pie.PieChart;
import com.ait.lienzo.client.core.animation.*;
import com.ait.lienzo.client.core.shape.Text;

import java.util.List;

import static com.ait.lienzo.charts.client.core.pie.animation.AbstractPieChartAnimation.buildAnimationProperties;

public class PieChartClearAnimation extends StackedTweeningAnimation
{

    public PieChartClearAnimation(PieChart pieChart, double width, double height, AnimationTweener tweener, double duration, IAnimationCallback callback)
    {
        super(pieChart, tweener, duration, callback);
        
        init(width, height);
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
