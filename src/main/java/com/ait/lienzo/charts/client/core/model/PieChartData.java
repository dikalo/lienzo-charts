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
 */

package com.ait.lienzo.charts.client.core.model;

import com.ait.lienzo.charts.client.core.model.DataTable.DataTableJSO;
import com.google.gwt.core.client.JavaScriptObject;

public final class PieChartData
{
    private final PieChartDataJSO m_jso;

    public PieChartData(PieChartDataJSO jso)
    {
        if (null != jso)
        {
            m_jso = jso;
        }
        else
        {
            m_jso = PieChartDataJSO.make();
        }
    }

    public PieChartData(final DataTable table, String categoriesProperty, String valuesProperty)
    {
        m_jso = PieChartDataJSO.make();

        m_jso.setDataTableJSO(table.getJSO());

        DataTableColumn categoriesCol = table.getColumn(categoriesProperty);

        DataTableColumn valuesCol = table.getColumn(valuesProperty);

        if (categoriesCol == null || !categoriesCol.getType().equals(DataTableColumn.DataTableColumnType.STRING)) throw new RuntimeException("PieChart only support STRING data types for categories property");

        if (valuesCol == null || !valuesCol.getType().equals(DataTableColumn.DataTableColumnType.NUMBER)) throw new RuntimeException("PieChart only support NUMERIC data types for values property");

        m_jso.setCategoriesProperty(categoriesProperty);

        m_jso.setValuesProperty(valuesProperty);
    }

    public final PieChartDataJSO getJSO()
    {
        return m_jso;
    }

    public final DataTable getDataTable()
    {
        return new DataTable(m_jso.getDataTableJSO());
    }

    public final String getCategoriesProperty()
    {
        return m_jso.getCategoriesProperty();
    }

    public final String getValuesProperty()
    {
        return m_jso.getValuesProperty();
    }

    public final int size()
    {
        return getDataTable().size();
    }

    public static final class PieChartDataJSO extends JavaScriptObject
    {
        protected PieChartDataJSO()
        {
        }

        public static final PieChartDataJSO make()
        {
            return createObject().cast();
        }

        public final native void setCategoriesProperty(String property) /*-{
			this.categoriesProperty = property;
        }-*/;

        public final native String getCategoriesProperty() /*-{
			return this.categoriesProperty;
        }-*/;

        public final native String getValuesProperty() /*-{
			return this.valuesProperty;
        }-*/;

        public final native void setValuesProperty(String property) /*-{
			this.valuesProperty = property;
        }-*/;

        public final native void setDataTableJSO(DataTableJSO dataTable) /*-{
			this.dataTable = dataTable;
        }-*/;

        public final native DataTableJSO getDataTableJSO() /*-{
			return this.dataTable;
        }-*/;
    }
}