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

package com.ait.lienzo.charts.client.core.xy.axis;

import com.ait.lienzo.charts.client.core.xy.XYChartData;
import com.ait.lienzo.charts.shared.core.types.AxisDirection;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CachedAxisBuilder<T> extends AxisBuilder<T>
{
    private List<AxisLabel>                       categoriesAxisLabels;

    private List<AxisLabel>                       valuesAxisLabels;

    final private Map<String, List<AxisValue<T>>> values = new HashMap<String, List<AxisValue<T>>>();

    public CachedAxisBuilder(XYChartData data, double chartSizeAttribute)
    {
        super(data, chartSizeAttribute);
        clear();
    }

    public CachedAxisBuilder(XYChartData data, double chartSizeAttribute, AxisDirection axisDirection)
    {
        super(data, chartSizeAttribute, axisDirection);
        clear();
    }

    protected abstract List<AxisLabel> buildValuesAxisLabels();

    protected abstract List<AxisLabel> buildCategoriesAxisLabels();

    protected abstract List<AxisValue<T>> buildValues(String modelProperty);

    @Override
    public List<AxisLabel> getValuesAxisLabels()
    {
        if (valuesAxisLabels == null)
        {
            valuesAxisLabels = buildValuesAxisLabels();
        }
        return valuesAxisLabels;
    }

    @Override
    public List<AxisLabel> getCategoriesAxisLabels()
    {
        if (categoriesAxisLabels == null)
        {
            categoriesAxisLabels = buildCategoriesAxisLabels();
        }
        return categoriesAxisLabels;
    }

    @Override
    public List<AxisValue<T>> getValues(String modelProperty)
    {
        if (!values.containsKey(modelProperty))
        {
            List<AxisValue<T>> _values = buildValues(modelProperty);
            values.put(modelProperty, _values);
        }
        return values.get(modelProperty);
    }

    @Override
    public void reload(XYChartData data, Collection<String> currentSeries, double chartSizeAttribute)
    {
        clear();
        super.reload(data, currentSeries, chartSizeAttribute);
    }

    @Override
    public void reload(double chartSizeAttribute)
    {
        clear();
        super.reload(chartSizeAttribute);
    }

    protected void clear()
    {
        valuesAxisLabels = null;
        categoriesAxisLabels = null;
        values.clear();
    }
}
