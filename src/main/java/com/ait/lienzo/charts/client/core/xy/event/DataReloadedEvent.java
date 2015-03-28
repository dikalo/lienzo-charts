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

package com.ait.lienzo.charts.client.core.xy.event;

import com.ait.lienzo.charts.client.core.xy.bar.BarChart;
import com.google.gwt.event.shared.GwtEvent;

public class DataReloadedEvent extends GwtEvent<DataReloadedEventHandler>
{
    public static Type<DataReloadedEventHandler> TYPE = new Type<DataReloadedEventHandler>();

    private BarChart                             chart;

    public DataReloadedEvent(BarChart chart)
    {
        this.chart = chart;
    }

    @Override
    public Type<DataReloadedEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(DataReloadedEventHandler handler)
    {
        handler.onDataReloaded(this);
    }

    public BarChart getChart()
    {
        return chart;
    }
}