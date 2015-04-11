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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ait.lienzo.charts.client.core.axis.Axis;
import com.ait.lienzo.charts.client.core.axis.DateAxis;
import com.ait.lienzo.charts.client.core.xy.XYChartData;

public final class DateAxisBuilder extends CachedAxisBuilder<Date>
{
    protected DateAxis axis;

    public DateAxisBuilder(XYChartData data, double chartSizeAttribute, Axis.AxisJSO jso)
    {
        super(data, chartSizeAttribute);
    }

    @Override
    protected List<AxisLabel> buildValuesAxisLabels()
    {
        return new ArrayList<AxisLabel>();
    }

    @Override
    protected List<AxisLabel> buildCategoriesAxisLabels()
    {
        return null;
    }
    @Override
    protected List<AxisValue<Date>> buildValues(String modelProperty)
    {
        return new ArrayList<AxisValue<Date>>();
    }
}