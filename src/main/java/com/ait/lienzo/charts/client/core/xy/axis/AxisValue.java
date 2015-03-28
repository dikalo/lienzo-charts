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

import java.util.Date;

public final class AxisValue<V>
{
    private V      m_value;

    private double m_position;
    
    public static final AxisValue<String> EMPTY_STRING_VALUE = new AxisValue<String>("", 0);
    public static final AxisValue<Double> EMPTY_NUMBER_VALUE = new AxisValue<Double>(null, 0);
    public static final AxisValue<Date> EMPTY_DATE_VALUE = new AxisValue<Date>(null, 0);

    public AxisValue(final V value, final double position)
    {
        m_value = value;

        m_position = position;
    }

    public final V getValue()
    {
        return m_value;
    }

    public final void setValue(final V value)
    {
        m_value = value;
    }

    public final double getPosition()
    {
        return m_position;
    }

    public final void setPosition(final double position)
    {
        m_position = position;
    }
}
