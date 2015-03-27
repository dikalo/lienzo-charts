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

package com.ait.lienzo.charts.client.core.xy.line;

import com.ait.lienzo.charts.client.core.xy.XYChartData;
import com.ait.lienzo.charts.client.core.xy.line.animation.LineChartCreateAnimation;
import com.ait.lienzo.client.core.animation.*;

/**
 * <p>Shortcuts for handling Line Charts using animations.</p>
 */
public final class LineChartAnimationHelper
{
    private LineChartAnimationHelper()
    {
    }

    public static final LineChart create(final LineChart lineChart, final AnimationTweener tweener, final double duration)
    {
        return create(lineChart, tweener, duration, null);
    }

    public static final LineChart create(final LineChart lineChart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        new LineChartCreateAnimation(lineChart, tweener, duration, callback).run();

        return lineChart;
    }

    public static final LineChart resize(final LineChart lineChart, final double width, final double height, final AnimationTweener tweener, final double duration)
    {
        return resize(lineChart, width, height, tweener, duration, null);
    }

    public static final LineChart resize(final LineChart lineChart, final double width, final double height, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        // TODO: new LineChartResizeAnimation(lineChart, width, height, tweener, duration, callback).run();

        return lineChart;
    }

    public static final LineChart clear(final LineChart lineChart, final AnimationTweener tweener, final double duration)
    {
        return clear(lineChart, tweener, duration, null);
    }

    public static final LineChart clear(final LineChart lineChart, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        // TODO: new LineChartClearAnimation(lineChart, tweener, duration, callback).run();

        return lineChart;
    }

    public static final LineChart reload(final LineChart lineChart, final XYChartData data, final AnimationTweener tweener, final double duration)
    {
        return reload(lineChart, data, tweener, duration, null);
    }

    public static final LineChart reload(final LineChart lineChart, final XYChartData data, final AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        // TODO
        return lineChart;
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
