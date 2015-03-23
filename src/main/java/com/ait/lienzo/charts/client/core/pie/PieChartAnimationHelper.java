package com.ait.lienzo.charts.client.core.pie;

import com.ait.lienzo.charts.client.core.model.PieChartData;
import com.ait.lienzo.charts.client.core.pie.animation.PieChartClearAnimation;
import com.ait.lienzo.charts.client.core.pie.animation.PieChartResizeAnimation;
import com.ait.lienzo.client.core.animation.*;

/**
 * <p>Shortcuts for handling Pie Charts using animations.</p>
 */
public final class PieChartAnimationHelper {
    
    public static PieChart create(final PieChart pieChart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback) {
        // Create chart children.
        pieChart.draw();

        // Resize the chart.
        new PieChartResizeAnimation(pieChart, pieChart.getChartWidth(), pieChart.getChartHeight(), tweener, duration, callback).run();
        
        return pieChart;
    }

    public static PieChart resize(final PieChart pieChart, final double width, final  double height, final AnimationTweener tweener, final double duration, final IAnimationCallback callback) {
        
        // Resize the chart.
        new PieChartResizeAnimation(pieChart, width, height, tweener, duration, callback).run();
        
        return pieChart;
    }

    public static PieChart clear(final PieChart pieChart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback) {
        new PieChartClearAnimation(pieChart, tweener, duration, callback).run();
        return pieChart;
    }

    public static PieChart reload(final PieChart pieChart, final PieChartData data, final AnimationTweener tweener, final double duration, final IAnimationCallback callback) {
        
        // If new data contains different properties on axis, clear current shapes.
        if (isCleanRequired(pieChart.getData(), data))
        {

            new PieChartClearAnimation(pieChart, tweener, duration, new AnimationCallback()
            {
                @Override
                public void onClose(IAnimation animation, IAnimationHandle handle)
                {
                    super.onClose(animation, handle);
                    pieChart.setData(data);
                    create(pieChart, tweener, duration, callback);
                }

            }).run();
        }

        // TODO

        return pieChart;
    }

    private static boolean isCleanRequired(PieChartData currentData, PieChartData newData)
    {
        if (currentData == null && newData == null) return false;
        if (currentData == null && newData != null) return false;
        if (newData == null && currentData != null) return true;
        String categoriesColumn = currentData.getCategoriesProperty();
        String newCategoriesColumn = newData.getCategoriesProperty();
        if (hasDataColumnChanged(categoriesColumn, newCategoriesColumn)) return true;
        String valuesColumn = currentData.getValuesProperty();
        String newValuesColumn = newData.getValuesProperty();
        return hasDataColumnChanged(valuesColumn, newValuesColumn);
    }

    private static boolean hasDataColumnChanged(String oldColumn, String newColumn)
    {
        if (oldColumn == null && newColumn != null) return true;
        if (oldColumn == null && newColumn == null) return false;
        if (oldColumn != null && newColumn == null) return true;
        if (oldColumn != null && !oldColumn.equals(newColumn)) return true;
        return false;
    }
    
    
}
