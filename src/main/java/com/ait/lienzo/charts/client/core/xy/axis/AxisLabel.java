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

public class AxisLabel
{
    private final String m_text;

    private final double m_position;

    protected AxisLabel(final String text, final double position)
    {
        m_text = text;
        
        m_position = position;
    }

    public String getText()
    {
        return m_text;
    }

    public double getPosition()
    {
        return m_position;
    }
}
