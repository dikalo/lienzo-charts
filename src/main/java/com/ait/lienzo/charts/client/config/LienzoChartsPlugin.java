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
 */

package com.ait.lienzo.charts.client.config;

import com.ait.lienzo.charts.client.core.ChartNodeType;
import com.ait.lienzo.charts.client.core.pie.PieChart;
import com.ait.lienzo.charts.client.core.xy.bar.BarChart;
import com.ait.lienzo.charts.client.core.xy.line.LineChart;
import com.ait.lienzo.client.core.config.AbstractLienzoCorePlugin;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.tooling.common.api.java.util.function.Supplier;

final class LienzoChartsPlugin extends AbstractLienzoCorePlugin
{
    LienzoChartsPlugin()
    {
        addFactorySupplier(ChartNodeType.PIE_CHART, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new PieChart.PieChartFactory();
            }
        });
        addFactorySupplier(ChartNodeType.BAR_CHART, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new BarChart.BarChartFactory();
            }
        });
        addFactorySupplier(ChartNodeType.LINE_CHART, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new LineChart.LineChartFactory();
            }
        });
    }

    @Override
    public final String getNameSpace()
    {
        return "LienzoCharts";
    }

    @Override
    public final String getVersion()
    {
        return "1.0";
    }
}
