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
import com.ait.lienzo.charts.client.core.axis.CategoryAxis;
import com.ait.lienzo.charts.client.core.model.DataTableColumn;
import com.ait.lienzo.charts.client.core.xy.XYChartData;
import com.ait.lienzo.charts.shared.core.types.AxisDirection;
import com.ait.lienzo.charts.shared.core.types.AxisType;

import java.util.LinkedList;
import java.util.List;

public final class CategoryAxisBuilder extends CachedAxisBuilder<String>
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
            throw new RuntimeException("You cannot draw a CategoryAxisBuilder using a non CategoryAxis");
        }
    }

    @Override
    protected List<AxisLabel> buildLabels()
    {
        List<AxisLabel> result = new LinkedList<AxisLabel>();
        DataTableColumn dataTableLabelsColumn = getDataSummary().getData().getDataTable().getColumn(getDataSummary().getData().getCategoryAxisProperty());
        String[] labelValues = dataTableLabelsColumn.getStringValues();
        int labelsCount = labelValues.length;
        //int seriesCount = getDataSummary().getNumSeries();
        double labelSize = getChartSizeAttribute() / labelsCount;
        for (int i = 0, j = labelsCount - 1; i < labelsCount; i++, j--)
        {
            String text = labelValues[i];
            double position = (getAxisDirection().equals(AxisDirection.DESC)) ? labelSize * i : labelSize * j;
            position += labelSize / 2;
            result.add(new AxisLabel(i, text, position));
        }
        return result;
    }

    @Override
    protected List<AxisValue<String>> buildValues(String modelProperty)
    {
        String[] values = getDataSummary().getData().getDataTable().getStringValues(modelProperty);
        int valuesCount = values.length;
        int seriesCount = getDataSummary().getNumSeries();

        List<AxisValue<String>> result = new LinkedList<AxisValue<String>>();
        if (values != null)
        {
            for (int i = 0, j = valuesCount - 1; i < valuesCount; i++, j--)
            {
                String value = (getAxisDirection().equals(AxisDirection.DESC)) ? values[i] : values[j];
                double valueSize = (getChartSizeAttribute() - (valuesCount + 1)) / valuesCount / seriesCount;
                double position = (valueSize * seriesCount * i) + valueSize/2;
                result.add(new AxisValue<String>(value, position));
            }
        }
        return result;
    }

}