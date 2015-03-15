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

package com.ait.lienzo.charts.client.core.xy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.ait.lienzo.charts.client.core.model.DataTable;
import com.ait.lienzo.charts.client.core.model.DataTableColumn;

public class XYChartDataSummary
{
    private XYChartData  data;

    private double       maxNumericValue;

    private Date         maxDateValue;

    private double       minNumericValue;

    private Date         minDateValue;

    private int          numSeries;

    // TODO: Refactor list impl to NFastArrayList?
    private List<String> addedSeries   = new ArrayList<String>();

    // TODO: Refactor list impl to NFastArrayList?
    private List<String> removedSeries = new ArrayList<String>();

    public XYChartDataSummary(XYChartData data)
    {
        this.data = data;

        build();
    }

    public XYChartDataSummary(XYChartData data, Collection<String> currentSerieNames)
    {
        // Build the summary.
        this(data);

        if (data != null && currentSerieNames != null)
        {
            // Chech added or removed series from last chart data.
            XYChartSeries[] newSeries = data.getSeries();

            for (XYChartSeries newSerie : newSeries)
            {
                if (!currentSerieNames.contains(newSerie.getName())) addedSeries.add(newSerie.getName());
            }
            for (String oldSerieName : currentSerieNames)
            {
                if (isSerieRemoved(newSeries, oldSerieName)) removedSeries.add(oldSerieName);
            }
        }
    }

    private boolean isSerieRemoved(XYChartSeries[] series, String serieName)
    {
        if (serieName == null || series == null) return false;

        for (XYChartSeries _serie : series)
        {
            if (_serie.getName().equals(serieName)) return false;
        }
        return true;
    }

    public void build()
    {
        if (data == null) return;

        final XYChartSeries[] series = data.getSeries();
        this.numSeries = series.length;
        for (int i = 0; i < series.length; i++)
        {
            XYChartSeries serie = series[i];
            //String categoryAxisProperty = data.getCategoryAxisProperty();
            String valuesAxisProperty = serie.getValuesAxisProperty();

            DataTable dataTable = data.getDataTable();
            //DataTableColumn categoryColumn = dataTable.getColumn(categoryAxisProperty);
            DataTableColumn valuesColumn = dataTable.getColumn(valuesAxisProperty);
            DataTableColumn.DataTableColumnType valuesColumnType = valuesColumn.getType();

            Object[] values = null;
            switch (valuesColumnType)
            {
                case NUMBER:
                    values = valuesColumn.getNumericValues();
                    if (values != null && values.length > 0)
                    {
                        for (int j = 0; j < values.length; j++)
                        {
                            Double value = (Double) values[j];
                            if (value >= maxNumericValue) maxNumericValue = value;
                            if (value <= minNumericValue) minNumericValue = value;
                        }
                    }
                    break;
                case DATE:
                    values = valuesColumn.getDateValues();
                    if (values != null && values.length > 0)
                    {
                        for (int j = 0; j < values.length; j++)
                        {
                            Date value = (Date) values[j];
                            if (value.after(maxDateValue)) maxDateValue = value;
                            if (value.before(minDateValue)) minDateValue = value;
                        }
                    }
                    break;
            }
        }
    }

    public double getMaxNumericValue()
    {
        return maxNumericValue;
    }

    public Date getMaxDateValue()
    {
        return maxDateValue;
    }

    public double getMinNumericValue()
    {
        return minNumericValue;
    }

    public Date getMinDateValue()
    {
        return minDateValue;
    }

    public XYChartData getData()
    {
        return data;
    }

    public int getNumSeries()
    {
        return numSeries;
    }

    public List<String> getAddedSeries()
    {
        return addedSeries;
    }

    public List<String> getRemovedSeries()
    {
        return removedSeries;
    }
}