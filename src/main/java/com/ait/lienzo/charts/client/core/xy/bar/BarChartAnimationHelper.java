package com.ait.lienzo.charts.client.core.xy.bar;

import com.ait.lienzo.charts.client.core.xy.XYChartData;
import com.ait.lienzo.charts.client.core.xy.bar.animation.BarChartClearAnimation;
import com.ait.lienzo.charts.client.core.xy.bar.animation.BarChartCreateAnimation;
import com.ait.lienzo.charts.client.core.xy.bar.animation.BarChartReloadAnimation;
import com.ait.lienzo.charts.client.core.xy.bar.animation.BarChartResizeAnimation;
import com.ait.lienzo.client.core.animation.*;

/**
 * <p>Shortcuts for handling Bar Charts using animations.</p>
 */
public final class BarChartAnimationHelper {
    
    public static BarChart create(final BarChart barChart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback) {
        new BarChartCreateAnimation(barChart, tweener, duration, callback).run();
        return barChart;
    }

    public static BarChart resize(final BarChart barChart, final double width, final  double height, final AnimationTweener tweener, final double duration, final IAnimationCallback callback) {
        new BarChartResizeAnimation(barChart, width, height, tweener, duration, callback).run();
        return barChart;
    }

    public static BarChart clear(final BarChart barChart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback) {
        new BarChartClearAnimation(barChart, tweener, duration, callback).run();
        return barChart;
    }

    public static BarChart reload(final BarChart barChart, final XYChartData data, final AnimationTweener tweener, final double duration, final IAnimationCallback callback) {
        if (isCleanRequired(barChart.getData(), data))
        {
            new BarChartClearAnimation(barChart, tweener, duration, new AnimationCallback()
            {
                @Override
                public void onClose(IAnimation animation, IAnimationHandle handle)
                {
                    super.onClose(animation, handle);
                    barChart.setData(data);
                    create(barChart, tweener, duration, callback);
                }
            }).run();
        }
        else if (barChart.getData() != null && data != null)
        {
            // Reload data as may have been updated.
            new BarChartReloadAnimation(barChart, data, tweener, duration, callback).run();
        }
        
        return barChart;
    }

    private static boolean isCleanRequired(final XYChartData currentData, final XYChartData newData)
    {
        if (currentData == null && newData == null) return false;
        if (currentData == null && newData != null) return false;
        if (newData == null && currentData != null) return true;
        String currentCategoryAxisProperty = currentData.getCategoryAxisProperty();
        String newCategoryAxisProperty = newData.getCategoryAxisProperty();
        if (currentCategoryAxisProperty == null && newCategoryAxisProperty != null) return true;
        if (currentCategoryAxisProperty == null && newCategoryAxisProperty == null) return false;
        if (currentCategoryAxisProperty != null && newCategoryAxisProperty == null) return true;
        if (currentCategoryAxisProperty != null && !currentCategoryAxisProperty.equals(newCategoryAxisProperty)) return true;
        if (currentData.getDataTable() != null && newData.getDataTable() != null && currentData.getDataTable().size() != newData.getDataTable().size()) return true;
        return false;
    }
    
}
