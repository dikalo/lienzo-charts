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

import com.ait.lienzo.charts.client.core.axis.Axis;
import com.ait.lienzo.charts.client.core.axis.NumericAxis;
import com.ait.lienzo.charts.client.core.xy.XYChartData;
import com.ait.lienzo.charts.shared.core.types.AxisDirection;
import com.ait.lienzo.charts.shared.core.types.AxisType;
import com.google.gwt.i18n.client.NumberFormat;

import java.util.LinkedList;
import java.util.List;

public final class NumericAxisBuilder extends CachedAxisBuilder<Double>
{
    private static final String       NULL_VALUE   = "0";

    private static final NumberFormat numberFormat = NumberFormat.getDecimalFormat();

    protected NumericAxis             axis;

    public NumericAxisBuilder(XYChartData data, double chartSizeAttribute, Axis.AxisJSO jso)
    {
        super(data, chartSizeAttribute);

        buildAxis(jso);
    }

    public NumericAxisBuilder(XYChartData data, double chartSizeAttribute, AxisDirection axisDirection, Axis.AxisJSO jso)
    {
        super(data, chartSizeAttribute, axisDirection);

        buildAxis(jso);
    }

    protected void buildAxis(Axis.AxisJSO jso)
    {
        if (Axis.getAxisTypeOf(jso) == AxisType.NUMBER)
        {
            this.axis = new NumericAxis((NumericAxis.NumericAxisJSO) jso);
        }
        else
        {
            throw new RuntimeException("You cannot draw a NumericAxisBuilder using a non NumericAxis");
        }
    }

    @Override
    protected List<AxisLabel> buildLabels()
    {
        //String modelProperty = getDataSummary().getData().getCategoryAxisProperty();
        //Double[] values = getDataSummary().getData().getDataTable().getNumericValues(modelProperty);
        int segments = axis.getSegments();
        Double maxValue = roundUp(getMaxValue());
        Double minValue = roundDown(getMinValue());

        double sizeAttributeIncrement = getChartSizeAttribute() / segments;
        double valueIncrement = (maxValue - minValue) / segments;

        List<AxisLabel> result = new LinkedList<AxisLabel>();
        for (int x = 0; x <= segments; x++)
        {
            double currentchartSizeAttribute = (getAxisDirection().equals(AxisDirection.DESC)) ? getChartSizeAttribute() - (sizeAttributeIncrement * x) : sizeAttributeIncrement * x;
            double currentValue = valueIncrement * x;
            String formattedValue = format(currentValue);
            result.add(new AxisLabel(x, formattedValue, currentchartSizeAttribute));
        }
        return result;
    }

    @Override
    protected List<AxisValue<Double>> buildValues(String modelProperty)
    {
        Double[] values = getDataSummary().getData().getDataTable().getNumericValues(modelProperty);
        //int segments = axis.getSegments();
        Double maxValue = getMaxValue();
        //Double minValue = getMinValue();

        List<AxisValue<Double>> result = new LinkedList<AxisValue<Double>>();
        if (values != null && values.length > 0)
        {
            for (int i = 0, j = values.length - 1; i < values.length; i++, j--)
            {
                Double value = (getAxisDirection().equals(AxisDirection.DESC)) ? values[i] : values[j];
                // Obtain width and height values for the shape.
                double shapeSize = getSizeForShape(getChartSizeAttribute(), value, maxValue);
                result.add(new AxisValue<Double>(value, shapeSize));
            }
        }
        return result;
    }

    protected Double getMinValue()
    {
        Double minValue = axis.getMinValue();
        if (minValue == null) return getDataSummary().getMinNumericValue();
        return minValue;
    }

    protected Double getMaxValue()
    {
        Double maxValue = axis.getMaxValue();
        if (maxValue == null) return getDataSummary().getMaxNumericValue();
        return maxValue;
    }

    protected double getSizeForShape(double chartSizeAttribute, double value, double maxValue)
    {
        return (chartSizeAttribute * value) / maxValue;
    }

    @Override
    public String format(Double value)
    {
        if (value != null) return getNumberFormat().format(value);
        return NULL_VALUE;
    }

    private NumberFormat getNumberFormat()
    {
        if (axis.getFormat() != null && axis.getFormat().trim().length() > 0) return NumberFormat.getFormat(axis.getFormat());
        else return numberFormat;
    }

    private Double roundUp(Double value)
    {
        return Math.ceil(value);
    }

    private Double roundDown(Double value)
    {
        return Math.floor(value);
    }
}