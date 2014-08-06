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

package com.ait.lienzo.charts.client.pie;

import java.util.ArrayList;

import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.IColor;

public final class PieChartData
{
    private static final PieChartEntry[]   M_EMPTY   = new PieChartEntry[0];

    private final ArrayList<PieChartEntry> m_entries = new ArrayList<PieChartEntry>();

    public PieChartData()
    {
    }

    public final PieChartData add(double value)
    {
        m_entries.add(new PieChartEntry(value, null, Color.getRandomHexColor()));

        return this;
    }

    public final PieChartData add(double value, IColor color)
    {
        m_entries.add(new PieChartEntry(value, null, color.getColorString()));

        return this;
    }

    public final PieChartData add(double value, String label)
    {
        m_entries.add(new PieChartEntry(value, label, Color.getRandomHexColor()));

        return this;
    }

    public final PieChartData add(double value, String label, IColor color)
    {
        m_entries.add(new PieChartEntry(value, label, color.getColorString()));

        return this;
    }

    final PieChartEntry[] getEntries()
    {
        return m_entries.toArray(M_EMPTY);
    }

    final int size()
    {
        return m_entries.size();
    }

    static final class PieChartEntry
    {
        private final double m_value;

        private final String m_label;

        private final String m_color;

        PieChartEntry(double value, String label, String color)
        {
            m_value = value;

            m_label = label;

            m_color = color;
        }

        final double getValue()
        {
            return m_value;
        }

        final String getLabel()
        {
            return m_label;
        }

        final String getColor()
        {
            return m_color;
        }
    }
}
