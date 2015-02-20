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

import java.util.LinkedList;
import java.util.List;

import com.ait.lienzo.charts.client.core.axis.Axis;
import com.ait.lienzo.charts.client.core.axis.CategoryAxis;
import com.ait.lienzo.charts.client.core.model.DataTableColumn;
import com.ait.lienzo.charts.client.core.xy.XYChartData;
import com.ait.lienzo.charts.shared.core.types.AxisDirection;
import com.ait.lienzo.charts.shared.core.types.AxisType;

public final class CategoryAxisBuilder extends AxisBuilder<String>
{
    protected CategoryAxis axis;

    public CategoryAxisBuilder(XYChartData data, double chartSizeAttribute, Axis.AxisJSO jso)
    {
        super(data, chartSizeAttribute);

        buildAxis(jso);
    }

    public CategoryAxisBuilder(XYChartData data, double chartSizeAttribute, AxisDirection axisDirection, Axis.AxisJSO jso)
    {
        super(data, chartSizeAttribute, axisDirection);

        buildAxis(jso);
    }

    protected void buildAxis(Axis.AxisJSO jso)
    {
        if (Axis.getAxisTypeOf(jso) == AxisType.CATEGORY)
        {
            this.axis = new CategoryAxis((CategoryAxis.CategoryAxisJSO) jso);
        }
        else
        {
            throw new RuntimeException("You cannot build a CategoryAxisBuilder using a non CategoryAxis");
        }
    }

    @Override
    public List<AxisLabel> getLabels()
    {
        List<AxisLabel> result = new LinkedList<AxisLabel>();
        //XYChartSeries[] series = getDataSummary().getData().getSeries();
        DataTableColumn dataTableLabelsColumn = getDataSummary().getData().getDataTable().getColumn(getDataSummary().getData().getCategoryAxisProperty());
        String[] labelValues = dataTableLabelsColumn.getStringValues();
        int labelsCount = labelValues.length;
        int seriesCount = getDataSummary().getNumSeries();
        final boolean desc = getAxisDirection() == AxisDirection.DESC;

        double labelSize = getChartSizeAttribute() / (seriesCount * labelsCount);

        for (int i = 0, j = labelsCount; i < labelsCount; i++, j--)
        {
            String text = labelValues[i];
            //int axisDivisions = axis.getSegments();
            double position = desc ? labelSize * i : labelSize * j;
            result.add(new AxisLabel(text, position * seriesCount));
        }
        return result;
    }

    @Override
    public List<AxisValue<String>> getValues(String modelProperty)
    {
        String[] values = getDataSummary().getData().getDataTable().getStringValues(modelProperty);
        int segments = axis.getSegments();
        int valuesCount = values.length;
        int seriesCount = getDataSummary().getNumSeries();

        List<AxisValue<String>> result = new LinkedList<AxisValue<String>>();

        if (values != null)
        {
            final boolean desc = getAxisDirection() == AxisDirection.DESC;

            for (int i = 0, j = valuesCount - 1; i < valuesCount; i++, j--)
            {
                String value = desc ? values[i] : values[j];
                int axisDivisions = axis.getSegments();
                double barSize = (getChartSizeAttribute() - (axisDivisions * (valuesCount + 1))) / valuesCount / seriesCount;
                double position = (barSize * seriesCount * i) + (segments * (i + 1));
                result.add(new AxisValue<String>(value, position));
            }
        }
        return result;
    }
}