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

package com.ait.lienzo.charts.client.core.pie;

import com.ait.lienzo.charts.client.core.model.PieChartData;
import com.ait.lienzo.charts.client.core.pie.animation.PieChartClearAnimation;
import com.ait.lienzo.charts.client.core.pie.animation.PieChartResizeAnimation;
import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;

/**
 * <p>Shortcuts for handling Pie Charts using animations.</p>
 */
public final class PieChartAnimationHelper
{
    private PieChartAnimationHelper()
    {
    }

    public static final PieChart create(final PieChart pieChart, final AnimationTweener tweener, final double duration)
    {
        return create(pieChart, tweener, duration, null);
    }

    public static final PieChart create(final PieChart pieChart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        // Create chart children.
        pieChart.draw();

        // Resize the chart.
        new PieChartResizeAnimation(pieChart, pieChart.getChartWidth(), pieChart.getChartHeight(), tweener, duration, callback).run();

        return pieChart;
    }

    public static final PieChart resize(final PieChart pieChart, final double width, final double height, final AnimationTweener tweener, final double duration)
    {
        return resize(pieChart, width, height, tweener, duration, null);
    }

    public static final PieChart resize(final PieChart pieChart, final double width, final double height, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        // Resize the chart.
        new PieChartResizeAnimation(pieChart, width, height, tweener, duration, callback).run();

        return pieChart;
    }

    public static final PieChart clear(final PieChart pieChart, final AnimationTweener tweener, final double duration)
    {
        return clear(pieChart, tweener, duration, null);
    }

    public static final PieChart clear(final PieChart pieChart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        new PieChartClearAnimation(pieChart, tweener, duration, callback).run();

        return pieChart;
    }

    public static PieChart reload(final PieChart pieChart, final PieChartData data, final AnimationTweener tweener, final double duration)
    {
        return reload(pieChart, data, tweener, duration, null);
    }

    public static PieChart reload(final PieChart pieChart, final PieChartData data, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
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

    private static final boolean isCleanRequired(PieChartData currentData, PieChartData newData)
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

    private static final boolean hasDataColumnChanged(String oldColumn, String newColumn)
    {
        if (oldColumn == null && newColumn != null) return true;
        if (oldColumn == null && newColumn == null) return false;
        if (oldColumn != null && newColumn == null) return true;
        if (oldColumn != null && !oldColumn.equals(newColumn)) return true;
        return false;
    }
}
