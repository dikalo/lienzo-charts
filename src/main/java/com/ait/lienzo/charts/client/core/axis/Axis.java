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

package com.ait.lienzo.charts.client.core.axis;

import com.ait.lienzo.charts.shared.core.types.AxisType;
import com.google.gwt.core.client.JavaScriptObject;

public abstract class Axis
{
    public static final AxisType getAxisTypeOf(final JavaScriptObject jso)
    {
        if (null == jso)
        {
            return null;
        }
        return AxisType.lookup(getAxisTypeString(jso));
    }

    private static final native String getAxisTypeString(JavaScriptObject jso)
    /*-{
		if (jso.axisType !== undefined) {
			return jso.axisType;
		}
		return null;
    }-*/;

    private final AxisJSO m_jso;

    public Axis(final String title, final AxisType type)
    {
        this(AxisJSO.make(title, type));
    }

    public Axis(final String title, final String format, final AxisType type)
    {
        this(AxisJSO.make(title, format, type));
    }

    public Axis(final AxisJSO jso)
    {
        m_jso = jso;
    }

    public AxisJSO getJSO()
    {
        return m_jso;
    }

    public Axis setFormat(String format)
    {
        m_jso.setFormat(format);

        return this;
    }

    public Axis setTitle(String title)
    {
        m_jso.setTitle(title);

        return this;
    }

    public Axis setSegments(int segments)
    {
        m_jso.setSegments(segments);

        return this;
    }

    public String getTitle()
    {
        return m_jso.getTitle();
    }

    public String getFormat()
    {
        return m_jso.getFormat();
    }

    public int getSegments()
    {
        return m_jso.getSegments();
    }

    public AxisType getAxisType()
    {
        return AxisType.lookup(m_jso.getAxisType());
    }

    public static class AxisJSO extends JavaScriptObject
    {
        protected AxisJSO()
        {
        }

        private static final native AxisJSO _make(String t)
        /*-{
			return {
				axisType : t,
				segments : 5
			};
        }-*/;

        public static final AxisJSO make(final AxisType type)
        {
            return _make(type.getValue());
        }

        public static final AxisJSO make(final String title, final AxisType type)
        {
            return make(type).setTitle(title);
        }

        public static final AxisJSO make(final String title, final String format, final AxisType type)
        {
            return make(title, type).setFormat(format);
        }

        public final native AxisJSO setFormat(String format) /*-{
			this.format = format;
			return this;
        }-*/;

        public final native AxisJSO setTitle(String title) /*-{
			this.title = title;
			return this;
        }-*/;

        public final native AxisJSO setType(String type) /*-{
			this.axisType = type;
			return this;
        }-*/;

        public final native AxisJSO setSegments(int segments) /*-{
			this.segments = segments;
			return this;
        }-*/;

        public final native String getAxisType() /*-{
			return this.axisType;
        }-*/;

        public final native String getFormat() /*-{
			return this.format;
        }-*/;

        public final native String getTitle() /*-{
			return this.title;
        }-*/;

        public final native int getSegments() /*-{
			return this.segments;
        }-*/;
    }
}
