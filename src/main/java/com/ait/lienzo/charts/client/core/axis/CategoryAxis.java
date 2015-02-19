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

package com.ait.lienzo.charts.client.core.axis;

public class CategoryAxis extends Axis
{
    private static final AxisType AXIS_TYPE = AxisType.CATEGORY;

    public CategoryAxis(String title)
    {
        super(title, AXIS_TYPE);
    }

    public CategoryAxis(String title, String format)
    {
        super(title, format, AXIS_TYPE);
    }

    public CategoryAxis(String title, String format, int size)
    {
        super(title, format, AXIS_TYPE);
    }

    public CategoryAxis(CategoryAxisJSO m_jso)
    {
        super(m_jso);
    }

    public CategoryAxisJSO getJSO()
    {
        return (CategoryAxisJSO) m_jso;
    }

    public static class CategoryAxisJSO extends AxisJSO
    {
        protected CategoryAxisJSO()
        {
        }
    }
}
