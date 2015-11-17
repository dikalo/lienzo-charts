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

import java.util.Date;

import com.ait.lienzo.charts.shared.core.types.AxisType;

public class DateAxis extends Axis
{
    public DateAxis(String title)
    {
        super(title, AxisType.NUMBER);
    }

    public DateAxis(String title, String format)
    {
        super(title, format, AxisType.NUMBER);
    }

    public DateAxis(String title, Date minValue, Date maxValue)
    {
        super(title, AxisType.NUMBER);

        setMinValue(minValue);

        setMaxValue(maxValue);
    }

    public DateAxis(String title, String format, Date minValue, Date maxValue)
    {
        super(title, format, AxisType.NUMBER);

        setMinValue(minValue);

        setMaxValue(maxValue);
    }

    public DateAxis(DateAxisJSO jso)
    {
        super(jso);
    }

    public DateAxisJSO getJSO()
    {
        return super.getJSO().cast();
    }

    public DateAxis setMaxValue(Date value)
    {
        getJSO().setMaxValue(value);

        return this;
    }

    public DateAxis setMinValue(Date value)
    {
        getJSO().setMinValue(value);

        return this;
    }

    public Date getMaxValue()
    {
        return getJSO().getMaxValue();
    }

    public Date getMinValue()
    {
        return getJSO().getMinValue();
    }

    public static class DateAxisJSO extends AxisJSO
    {
        protected DateAxisJSO()
        {
        }

        public final native DateAxisJSO setMaxValue(Date maxValue) /*-{
			this.maxValue = maxValue;
			return this;
        }-*/;

        public final native DateAxisJSO setMinValue(Date minValue) /*-{
			this.minValue = minValue;
			return this;
        }-*/;

        public final native Date getMinValue() /*-{
			if (!this.minValue)
				return null;
			return this.minValue;
        }-*/;

        public final native Date getMaxValue() /*-{
			if (!this.maxValue)
				return null;
			return this.maxValue;
        }-*/;
    }
}
