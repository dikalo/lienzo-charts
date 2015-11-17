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

public class NumericAxis extends Axis
{
    public NumericAxis(String title)
    {
        super(title, AxisType.NUMBER);
    }

    public NumericAxis(String title, String format)
    {
        super(title, format, AxisType.NUMBER);
    }

    public NumericAxis(String title, double minValue, double maxValue)
    {
        super(title, AxisType.NUMBER);

        setMinValue(minValue);

        setMaxValue(maxValue);
    }

    public NumericAxis(String title, String format, double minValue, double maxValue)
    {
        super(title, format, AxisType.NUMBER);

        setMinValue(minValue);

        setMaxValue(maxValue);
    }

    public NumericAxis(NumericAxisJSO jso)
    {
        super(jso);
    }

    public NumericAxisJSO getJSO()
    {
        return super.getJSO().cast();
    }

    public NumericAxis setMaxValue(double value)
    {
        getJSO().setMaxValue(value);

        return this;
    }

    public NumericAxis setMinValue(double value)
    {
        getJSO().setMinValue(value);

        return this;
    }

    public Double getMaxValue()
    {
        return getJSO().getMaxValue();
    }

    public Double getMinValue()
    {
        return getJSO().getMinValue();
    }

    public static class NumericAxisJSO extends AxisJSO
    {
        protected NumericAxisJSO()
        {
        }

        public final native NumericAxisJSO setMaxValue(Double maxValue) /*-{
			this.maxValue = maxValue;
			return this;
        }-*/;

        public final native NumericAxisJSO setMinValue(Double minValue) /*-{
			this.minValue = minValue;
			return this;
        }-*/;

        public final native Double getMinValue() /*-{
			return this.minValue;
        }-*/;

        public final native Double getMaxValue() /*-{
			return this.maxValue;
        }-*/;
    }
}
