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
   
   Author: Roger Martinez - Red Hat
 */

package com.ait.lienzo.charts.client.core.model;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

/**
 * <p>Columns are used to represent data types and other definition parameters for the values of the table entries.</p>
 * 
 * <p>There exist three type of columns depending on the data type of the value that the column stores:</p>
 * <ul>
 *     <li><code>String</code> column types - Contains string values.</li>
 *     <li><code>Numberic</code> column types - Contains numeric values.</li>
 *     <li><code>Date</code> column types - Contains date values.</li>
 * </ul>
 */
public final class DataTableColumn
{
    private final DataTableColumnJSO m_jso;

    public DataTableColumn(String id, DataTableColumnType type)
    {
        this(DataTableColumnJSO.make(id, type));
    }

    public DataTableColumn(DataTableColumnJSO jso)
    {
        m_jso = jso;
    }

    public static final class DataTableColumnJSO extends JsArrayMixed
    {
        protected DataTableColumnJSO()
        {
        }

        public static final DataTableColumnJSO make()
        {
            return JsArray.createArray().cast();
        }

        public static final DataTableColumnJSO make(String id, DataTableColumnType type)
        {
            DataTableColumnJSO dataTableColumn = make();

            dataTableColumn.setId(id);

            dataTableColumn.setType(type);

            return dataTableColumn;
        }

        public final native void setId(String id) /*-{
			this.id = id;
        }-*/;

        public final native void setType(DataTableColumnType type) /*-{
			this.type = type;
        }-*/;

        public final native String getId() /*-{
			return this.id;
        }-*/;

        public final native DataTableColumnType getType() /*-{
			return this.type;
        }-*/;
    }

    public final String getId()
    {
        return m_jso.getId();
    }

    public final DataTableColumnType getType()
    {
        return m_jso.getType();
    }

    public final int length()
    {
        return m_jso.length();
    }

    protected DataTableColumnJSO getJSO()
    {
        return m_jso;
    }

    public final void addValue(String value)
    {
        m_jso.push(value);
    }

    public final void addValue(double value)
    {
        m_jso.push(value);
    }

    public final void addValue(Date value)
    {
        String s = fromDate(value);

        m_jso.push(s);
    }

    public final void setValue(int pos, String value)
    {
        m_jso.set(pos, value);
    }

    public final void setValue(int pos, double value)
    {
        m_jso.set(pos, value);
    }

    public final void setValue(int pos, Date value)
    {
        m_jso.set(pos, fromDate(value));
    }

    public final String getStringValue(int pos)
    {
        return m_jso.getString(pos);
    }

    public final Double getNumericValue(int pos)
    {
        return m_jso.getNumber(pos);
    }

    public final Date getDateValue(int pos)
    {
        return toDate(m_jso.getString(pos));
    }

    public final String[] getStringValues()
    {
        if (!m_jso.getType().equals(DataTableColumnType.STRING))
        {
            return null;
        }
        final int leng = m_jso.length();

        String[] result = new String[leng];

        for (int x = 0; x < leng; x++)
        {
            result[x] = getStringValue(x);
        }
        return result;
    }

    public final Double[] getNumericValues()
    {
        if (!m_jso.getType().equals(DataTableColumnType.NUMBER))
        {
            return null;
        }
        final int leng = m_jso.length();

        Double[] result = new Double[leng];

        for (int x = 0; x < leng; x++)
        {
            result[x] = getNumericValue(x);
        }
        return result;
    }

    public final Date[] getDateValues()
    {
        if (!m_jso.getType().equals(DataTableColumnType.DATE))
        {
            return null;
        }
        final int leng = m_jso.length();

        Date[] result = new Date[leng];

        for (int x = 0; x < leng; x++)
        {
            result[x] = getDateValue(x);
        }
        return result;
    }

    protected final Date toDate(String value)
    {
        return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_FULL).parse(value);
    }

    protected final String fromDate(Date value)
    {
        return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_FULL).format(value);
    }

    public enum DataTableColumnType
    {
        /**
         * Text column
         */
        STRING("string"),
        /**
         * Integer or decimal column
         */
        NUMBER("number"),
        /**
         * Date column (without time)
         */
        DATE("date");

        /**
         * Find the column type by name.
         *
         * @param name The column type name.
         * @return the type corresponding to the provided name.
         */
        public static DataTableColumnType lookup(String name)
        {
            for (DataTableColumnType columnType : DataTableColumnType.values())
            {
                if (columnType.getName().equals(name))
                {
                    return columnType;
                }
            }
            return null;
        }

        private final String m_name;

        private DataTableColumnType(String name)
        {
            m_name = name;
        }

        public String getName()
        {
            return m_name;
        }
    }
}
