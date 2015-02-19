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

public abstract class AxisBuilder<T>
{
    protected XYChartDataSummary dataSummary;

    protected double             chartSizeAttribute;

    protected AxisDirection      axisDirection;

    public AxisBuilder(XYChartData data, double chartSizeAttribute)
    {
        this.dataSummary = new XYChartDataSummary(data);
        this.chartSizeAttribute = chartSizeAttribute;
        this.axisDirection = AxisDirection.DESC;
    }

    public AxisBuilder(XYChartData data, double chartSizeAttribute, AxisDirection axisDirection)
    {
        this(data, chartSizeAttribute);
        this.axisDirection = axisDirection;
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
     * @return Serie's axis values.
     */
    public abstract List<AxisValue<T>> getValues(String modelProperty);

    public void reload(XYChartData data, Collection<String> currentSeries, double chartSizeAttribute)
    {
        // Rebuild data summary as columns, series and values can have been modified.
        this.dataSummary = new XYChartDataSummary(data, currentSeries);
        this.chartSizeAttribute = chartSizeAttribute;
    }

    public String format(T value)
    {
        // TODO: Override by subclasses. Format each type using datatable column -> pattern attribute.
        return value.toString();
    }

    public XYChartDataSummary getDataSummary()
    {
        return this.dataSummary;
    }

    public class AxisLabel
    {
        protected String text;

        protected double position;

        protected AxisLabel(String text, double position)
        {
            this.text = text;
            this.position = position;
        }

        public String getText()
        {
            return text;
        }

        public double getPosition()
        {
            return position;
        }
    }

    public class AxisValue<T>
    {
        protected T      value;

        protected double position;

        protected AxisValue(T value, double position)
        {
            this.value = value;
            this.position = position;
        }

        public double getPosition()
        {
            return position;
        }

        public void setPosition(double position)
        {
            this.position = position;
        }

        public T getValue()
        {
            return value;
        }

        public void setValue(T value)
        {
            this.value = value;
        }

    }

    public enum AxisDirection
    {
        ASC, DESC;
    }
}