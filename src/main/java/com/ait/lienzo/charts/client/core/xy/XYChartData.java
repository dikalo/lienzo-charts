/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.charts.client.core.xy;

import com.ait.lienzo.charts.client.core.model.DataTable;
import com.ait.lienzo.charts.client.core.xy.XYChartSeries.XYChartSeriesJSO;
import com.google.gwt.core.client.JsArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public final class XYChartData implements Iterable<XYChartSeries>
{
    private final XYChartDataJSO m_jso;

    public XYChartData(XYChartDataJSO jso)
    {
        if (null != jso)
        {
            m_jso = jso;
        }
        else
        {
            m_jso = XYChartDataJSO.make();
        }
    }

    public XYChartData(DataTable dataTable)
    {
        this(XYChartDataJSO.make());

        m_jso.setDataTable(dataTable);
    }

    public final XYChartData setCategoryAxisProperty(String categoryAxisProperty)
    {
        if ((categoryAxisProperty != null) && (categoryAxisProperty.trim().length() != 0))
        {
            if (getDataTable().getColumn(categoryAxisProperty) != null)
            {
                m_jso.setCategoryAxisProperty(categoryAxisProperty);
            }
            else
            {
                throw new RuntimeException("The data model property [" + categoryAxisProperty + "] does not exist in the model.");
            }
        }
        return this;
    }

    public final XYChartData addSeries(XYChartSeries series)
    {
        if (series == null)
        {
            return null;
        }
        if (getSeries(series.getName()) != null)
        {
            throw new RuntimeException("A series with name [" + series.getName() + "] already exist.");
        }
        m_jso.push(series.getJSO());

        return this;
    }

    public final XYChartData removeSeries(XYChartSeries series)
    {
        int pos = getSeriesPosition(series.getName());

        if (pos >= 0)
        {
            m_jso.removeSeries(pos);
        }
        return this;
    }

    public final DataTable getDataTable()
    {
        return m_jso.getDataTable();
    }

    public final String getCategoryAxisProperty()
    {
        return m_jso.getCategoryAxisProperty();
    }

    public final int size()
    {
        return m_jso.length();
    }

    public final XYChartDataJSO getJSO()
    {
        return m_jso;
    }

    public final Collection<XYChartSeries> toCollection()
    {
        final int size = size();

        // TODO: Refactor list impl to NFastArrayList?
        ArrayList<XYChartSeries> list = new ArrayList<XYChartSeries>(size);

        for (int i = 0; i < size; i++)
        {
            list.add(new XYChartSeries(m_jso.get(i)));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public final Iterator<XYChartSeries> iterator()
    {
        return toCollection().iterator();
    }

    public XYChartSeries[] getSeries()
    {
        final int size = size();

        XYChartSeries[] list = new XYChartSeries[size];

        for (int i = 0; i < size; i++)
        {
            list[i] = new XYChartSeries(m_jso.get(i));
        }
        return list;
    }

    public XYChartSeries getSeries(String name)
    {
        final int size = size();

        for (int i = 0; i < size; i++)
        {
            XYChartSeriesJSO jso = m_jso.get(i);

            if (name.equals(jso.getName()))
            {
                return new XYChartSeries(jso);
            }
        }
        return null;
    }

    public int getSeriesPosition(String name)
    {
        final int size = size();

        for (int i = 0; i < size; i++)
        {
            XYChartSeriesJSO jso = m_jso.get(i);

            if (name.equals(jso.getName()))
            {
                return i;
            }
        }
        return -1;
    }

    public static final class XYChartDataJSO extends JsArray<XYChartSeries.XYChartSeriesJSO>
    {
        protected XYChartDataJSO()
        {
        }

        public static final XYChartDataJSO make()
        {
            return JsArray.createArray().cast();
        }

        public final native void setCategoryAxisProperty(String property) /*-{
			this.categoryAxisProperty = property;
        }-*/;

        public final native String getCategoryAxisProperty() /*-{
			return this.categoryAxisProperty;
        }-*/;

        public final native void setDataTable(DataTable dataTable) /*-{
			this.dataTable = dataTable;
        }-*/;

        public final native DataTable getDataTable() /*-{
			return this.dataTable;
        }-*/;

        public final native void removeSeries(int pos) /*-{
			this.splice(pos, 1);
        }-*/;
    }
}
