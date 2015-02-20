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

import java.util.Collection;
import java.util.List;

import com.ait.lienzo.charts.client.core.xy.XYChartData;
import com.ait.lienzo.charts.client.core.xy.XYChartDataSummary;
import com.ait.lienzo.charts.shared.core.types.AxisDirection;

public abstract class AxisBuilder<T>
{
    private XYChartDataSummary m_dataSummary;

    private double             m_chartSizeAttribute;

    private AxisDirection      m_axisDirection;

    public AxisBuilder(XYChartData data, double chartSizeAttribute)
    {
        this(data, chartSizeAttribute, AxisDirection.DESC);
    }

    public AxisBuilder(XYChartData data, double chartSizeAttribute, AxisDirection axisDirection)
    {
        m_dataSummary = new XYChartDataSummary(data);

        m_chartSizeAttribute = chartSizeAttribute;

        m_axisDirection = axisDirection;
    }

    public XYChartDataSummary getDataSummary()
    {
        return m_dataSummary;
    }

    public double getChartSizeAttribute()
    {
        return m_chartSizeAttribute;
    }

    public AxisDirection getAxisDirection()
    {
        return m_axisDirection;
    }

    /**
     * Get axis labels for all series
     * TODO: Cache.
     * @return All serie axis labels.
     */
    public abstract List<AxisLabel> getLabels();

    /**
     * Get axis values for a given property in the datatable model..
     * TODO: Cache.
     * @parm modelProperty The property in the datatable model.. 
     * @return Series axis values.
     */
    public abstract List<AxisValue<T>> getValues(String modelProperty);

    public void reload(XYChartData data, Collection<String> currentSeries, double chartSizeAttribute)
    {
        // Rebuild data summary as columns, series and values can have been modified.
        m_dataSummary = new XYChartDataSummary(data, currentSeries);

        m_chartSizeAttribute = chartSizeAttribute;
    }

    public String format(T value)
    {
        // TODO: Override by subclasses. Format each type using datatable column -> pattern attribute.
        return value.toString();
    }
}