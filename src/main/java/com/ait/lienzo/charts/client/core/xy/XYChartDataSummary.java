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

    final private double[]       maxNumericValue = new double[2];

    final private Date[]         maxDateValue = new Date[2];

    final private double[]       minNumericValue = new double[2];

    final private Date[]         minDateValue = new Date[2];

    private int          numSeries;

    // TODO: Refactor list impl to NFastArrayList?
    private List<String> addedSeries   = new ArrayList<String>();

    // TODO: Refactor list impl to NFastArrayList?
    private List<String> removedSeries = new ArrayList<String>();

    private AxisSummary categoriesAxisSummary;

    private AxisSummary valuesAxisSummary;

    /**
     * <p>Data summary contract for both axis: categories and values.</p> 
     */
    public interface AxisSummary {
        double getMaxNumericValue();
        double getMinNumericValue();
        Date getMaxDateValue();
        Date getMinDateValue();
    }
    
    public XYChartDataSummary(final XYChartData data)
    {
        this.data = data;

        // Build both data summary instances for the categories and values axis.
        buildCategoriesAxisSummary();
        buildValuesAxisSummary();
    }

    public XYChartDataSummary(final XYChartData data, final Collection<String> currentSerieNames)
    {
        // Build the summary.
        this(data);

        if (data != null && currentSerieNames != null)
        {
            // Check added or removed series from last chart data.
            XYChartSeries[] newSeries = data.getSeries();

            for (XYChartSeries newSerie : newSeries)
            {
                if (!currentSerieNames.contains(newSerie.getName())) addedSeries.add(newSerie.getName());
            }
            for (String oldSerieName : currentSerieNames)
            {
                if (isSeriesRemoved(newSeries, oldSerieName)) removedSeries.add(oldSerieName);
            }
        }
    }

    private boolean isSeriesRemoved(final XYChartSeries[] series, final String serieName)
    {
        if (serieName == null || series == null) return false;

        for (XYChartSeries _serie : series)
        {
            if (_serie.getName().equals(serieName)) return false;
        }
        return true;
    }

    public void buildValuesAxisSummary()
    {
        if (data == null) return;

        // Obtain limit values for number and date columns, for each series.
        final XYChartSeries[] series = data.getSeries();
        this.numSeries = series.length;
        final DataTable dataTable = data.getDataTable();
        for (int i = 0; i < series.length; i++)
        {
            final XYChartSeries serie = series[i];
            final String valuesAxisProperty = serie.getValuesAxisProperty();
            final DataTableColumn valuesColumn = dataTable.getColumn(valuesAxisProperty);
            final DataTableColumn.DataTableColumnType valuesColumnType = valuesColumn.getType();
            switch (valuesColumnType) {
                case NUMBER:
                    final Double[] columnLimits = (Double[]) getLimitValues(valuesColumn, maxNumericValue[1], minNumericValue[1]);
                    if (columnLimits[0] <= minNumericValue[1]) minNumericValue[1] = columnLimits[0];
                    if (columnLimits[1] >= maxNumericValue[1]) maxNumericValue[1] = columnLimits[1];
                    break;
                case DATE:
                    final Date[] _columnLimits = (Date[]) getLimitValues(valuesColumn, maxDateValue[1], minDateValue[1]);
                    if (_columnLimits[1].after(maxDateValue[1])) maxDateValue[1] = _columnLimits[1];
                    if (_columnLimits[0].before(minDateValue[1])) minDateValue[1] = _columnLimits[0];
                    break;
                case STRING:
                    break;
                default:
                    break;
            }
        }
        
        // Build the axis summary instance.
        this.valuesAxisSummary  = new AxisSummary() {
            @Override
            public double getMaxNumericValue() {
                return maxNumericValue[1];
            }

            @Override
            public Date getMaxDateValue() {
                return maxDateValue[1];
            }

            @Override
            public double getMinNumericValue() {
                return minNumericValue[1];
            }

            @Override
            public Date getMinDateValue() {
                return minDateValue[1];
            }
        };
    }
    
    private void buildCategoriesAxisSummary() {
        final DataTable dataTable = data.getDataTable();
        final DataTableColumn valuesColumn = dataTable.getColumn(getData().getCategoryAxisProperty());
        final DataTableColumn.DataTableColumnType valuesColumnType = valuesColumn.getType();

        // Obtain limit values for number and date columns, for category axis column,
        switch (valuesColumnType) {
            case NUMBER:
                final Double[] columnLimits = (Double[]) getLimitValues(valuesColumn, maxNumericValue[0], minNumericValue[0]);
                if (columnLimits[0] <= minNumericValue[0]) minNumericValue[0] = columnLimits[0];
                if (columnLimits[1] >= maxNumericValue[0]) maxNumericValue[0] = columnLimits[1];
                break;
            case DATE:
                final Date[] _columnLimits = (Date[]) getLimitValues(valuesColumn, maxDateValue[0], minDateValue[0]);
                if (_columnLimits[1].after(maxDateValue[0])) maxDateValue[0] = _columnLimits[1];
                if (_columnLimits[0].before(minDateValue[0])) minDateValue[0] = _columnLimits[0];
                break;
            case STRING:
                break;
            default:
                break;
        }
        
        // Build the axis summary instance.
        this.categoriesAxisSummary = new AxisSummary() {
            @Override
            public double getMaxNumericValue() {
                return maxNumericValue[0];
            }

            @Override
            public Date getMaxDateValue() {
                return maxDateValue[0];
            }

            @Override
            public double getMinNumericValue() {
                return minNumericValue[0];
            }

            @Override
            public Date getMinDateValue() {
                return minDateValue[0];
            }
        };
    }
    
    private Object[] getLimitValues(final DataTableColumn valuesColumn, final Object maxValue, final Object minValue) {
        final DataTableColumn.DataTableColumnType valuesColumnType = valuesColumn.getType();
        
        Object[] values = null;
        switch (valuesColumnType)
        {
            case NUMBER:
                values = valuesColumn.getNumericValues();
                if (values != null && values.length > 0)
                {
                    double maxNumericValue = maxValue != null ? ((Double)maxValue) : 0d;
                    double minNumericValue = minValue != null ? ((Double)minValue) : 0d;
                    final Double[] result = new Double[] {minNumericValue, maxNumericValue};

                    for (int j = 0; j < values.length; j++)
                    {
                        Double value = (Double) values[j];
                        if (value >= maxNumericValue) maxNumericValue = value;
                        if (value <= minNumericValue) minNumericValue = value;
                    }
                    
                    result[0] = minNumericValue;
                    result[1] = maxNumericValue;
                    return result;
                }
                break;
            case DATE:
                values = valuesColumn.getDateValues();
                if (values != null && values.length > 0)
                {
                    Date maxDateValue = maxValue != null ? ((Date)maxValue) : null;;
                    Date minDateValue = minValue != null ? ((Date)minValue) : null;;
                    final Date[] result = new Date[] {minDateValue, maxDateValue};

                    for (int j = 0; j < values.length; j++)
                    {
                        Date value = (Date) values[j];
                        if (value.after(maxDateValue)) maxDateValue = value;
                        if (value.before(minDateValue)) minDateValue = value;
                    }

                    result[0] = minDateValue;
                    result[1] = maxDateValue;
                    return result;
                }
                break;
            case STRING:
                break;
            default:
                break;
        }
        
        return null;
    }

    public AxisSummary getCategoriesAxisSummary() {
        return categoriesAxisSummary;
    }

    public AxisSummary getValuesAxisSummary() {
        return valuesAxisSummary;
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