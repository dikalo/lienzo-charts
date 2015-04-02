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

package com.ait.lienzo.charts.client.core.xy.bar;

import com.ait.lienzo.charts.client.core.ChartNodeType;
import com.ait.lienzo.charts.client.core.axis.Axis;
import com.ait.lienzo.charts.client.core.resizer.ChartResizeEvent;
import com.ait.lienzo.charts.client.core.xy.XYChart;
import com.ait.lienzo.charts.client.core.xy.XYChartData;
import com.ait.lienzo.charts.client.core.xy.XYChartSeries;
import com.ait.lienzo.charts.client.core.xy.axis.AxisBuilder;
import com.ait.lienzo.charts.client.core.xy.axis.AxisValue;
import com.ait.lienzo.charts.client.core.xy.axis.CategoryAxisBuilder;
import com.ait.lienzo.charts.client.core.xy.axis.NumericAxisBuilder;
import com.ait.lienzo.charts.client.core.xy.bar.animation.BarChartResizeAnimation;
import com.ait.lienzo.charts.client.core.xy.event.ValueSelectedEvent;
import com.ait.lienzo.charts.shared.core.types.AxisDirection;
import com.ait.lienzo.charts.shared.core.types.AxisType;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.google.gwt.json.client.JSONObject;

import java.util.*;

import static com.ait.lienzo.client.core.animation.AnimationProperties.toPropertyList;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.ALPHA;
import static com.ait.lienzo.client.core.animation.AnimationTweener.LINEAR;

/**
 * <p>XY chart implementation using rectangles as shapes for values.</p>
 * <p>It can be drawn as a bar chart or a column chart depending on the <code>CHART_ORIENTATION</code> attribute.</p>
 *  
 * <p>Attributes:</p>
 * <ul>
 *     <li><code>X</code>: The chart X position.</li>
 *     <li><code>Y</code>: The chart Y position.</li>
 *     <li><code>WIDTH</code>: The chart width.</li>
 *     <li><code>HEIGHT</code>: The chart height.</li>
 *     <li><code>MARGIN_LEFT</code>: The left margin.</li>
 *     <li><code>MARGIN_RIGHT</code>: The right margin.</li>
 *     <li><code>MARGIN_TOP</code>: The top margin.</li>
 *     <li><code>MARGIN_BOTTOM</code>: The bottom margin.</li>
 *     <li><code>NAME</code>: The chart name, used as title.</li>
 *     <li><code>SHOW_TITLE</code>: Flag for title visibility.</li>
 *     <li><code>FONT_FAMILY</code>: The chart font family.</li>
 *     <li><code>FONT_STYLE</code>: The chart font style.</li>
 *     <li><code>FONT_SIZE</code>: The chart font size.</li>
 *     <li><code>RESIZABLE</code>: Add or avoid the use of the chart resizer.</li>
 *     <li><code>LEGEND_POSITION</code>: The chart legend position.</li>
 *     <li><code>LEGEND_ALIGN</code>: The chart legend alignment.</li>
 *     <li><code>XY_CHART_DATA</code>: The chart data.</li>
 *     <li><code>CATEGORY_AXIS</code>: The chart category axis.</li>
 *     <li><code>VALUES_AXIS</code>: The chart values axis.</li>
 *     <li><code>SHOW_CATEGORIES_AXIS_TITLE</code>: Show the title for categoreis axis.</li>
 *     <li><code>SHOW_VALUES_AXIS_TITLE</code>: Show the title for values axis.</li>
 *     <li><code>CATEGORIES_AXIS_LABELS_POSITION</code>: The position for the categories axis labels.</li>
 *     <li><code>VALUES_AXIS_LABELS_POSITION</code>: The position for the values axis labels.</li>
 *     <li><code>ALIGN</code>: The chart alignment.</li>
 *     <li><code>DIRECTION</code>: The chart direction.</li>
 *     <li><code>ORIENTATION</code>: The chart orientation (Bar or Column).</li>
 * </ul>
 * 
 */
public class BarChart extends XYChart<BarChart>
{
    // Default separation size between bars.
    public static final double         BAR_SEPARATION          = 2;

    public static final double         BAR_MAX_SIZE            = 75;

    // If bar are too big, use this proportion (30%).
    public static final double         BAR_MAX_SIZE_PROPORTION = 0.3;

    final Map<String, List<Rectangle>> seriesValues            = new LinkedHashMap<String, List<Rectangle>>(); // The rectangles that represents the data.

    protected BarChart(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(node, ChartNodeType.BAR_CHART, ctx);
    }

    public BarChart()
    {
        super(ChartNodeType.BAR_CHART);

        getMetaData().put("creator", "Roger Martinez");
    }

    @Override
    public BarChart init()
    {
        return BarChartAnimationHelper.create(this, LINEAR, getDefaultAnimationDuration());
    }

    @Override
    public BarChart init(double duration)
    {
        return BarChartAnimationHelper.create(this, LINEAR, Math.max(duration, 1));
    }

    @Override
    public BarChart init(AnimationTweener tweener, double duration)
    {
        return BarChartAnimationHelper.create(this, ((tweener != null) ? tweener : LINEAR), Math.max(duration, 1));
    }

    public BarChart reload(XYChartData data)
    {
        return BarChartAnimationHelper.reload(this, data, LINEAR, getDefaultAnimationDuration());
    }

    public BarChart reload(XYChartData data, double duration)
    {
        return BarChartAnimationHelper.reload(this, data, LINEAR, Math.max(duration, 1));
    }

    public BarChart reload(XYChartData data, AnimationTweener tweener, double duration)
    {
        return BarChartAnimationHelper.reload(this, data, ((tweener != null) ? tweener : LINEAR), Math.max(duration, 1));
    }

    public static class BarChartFactory extends XYChartFactory<BarChart>
    {
        public BarChartFactory()
        {
            super(ChartNodeType.BAR_CHART);
        }

        @Override
        public boolean addNodeForContainer(IContainer<?, ?> container, Node<?> node, ValidationContext ctx)
        {
            return false;
        }

        @Override
        protected BarChart container(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new BarChart(node, ctx);
        }
    }

    @Override
    protected void onChartResize(ChartResizeEvent event)
    {
        boolean apply = event.isApply();

        if (apply)
        {
            final double w = event.getWidth() - getMarginLeft() - getMarginRight();
            final double h = event.getHeight() - getMarginTop() - getMarginBottom();
            // Apply resize to bar chart.
            new BarChartResizeAnimation(this, w, h, LINEAR, getDefaultAnimationDuration(), null).run();
        }
        super.onChartResize(event);
    }

    public BarChart buildSeriesValues(final XYChartSeries series, final int numSeries)
    {
        final List<? extends AxisValue<?>> xAxisValues = categoriesAxisBuilder.getValues(getData().getCategoryAxisProperty());
        final List<? extends AxisValue<?>> yAxisValues = valuesAxisBuilder.getValues(series.getValuesAxisProperty());

        if (xAxisValues != null)
        {
            final List<Rectangle> bars = new LinkedList<Rectangle>();
            for (int i = 0; i < xAxisValues.size(); i++)
            {
                final AxisValue<?> axisValue = xAxisValues.get(i);
                final AxisValue<?> yAxisValue = yAxisValues.get(i);
                final Object value = axisValue.getValue();
                final Object yValue = yAxisValue.getValue();
                final String xValueFormatted = categoriesAxisBuilder.format(value);
                final String yValueFormatted = valuesAxisBuilder.format(yValue);

                final Rectangle bar = new Rectangle(0, 0);
                bar.setID(buildId("value", numSeries, i));
                bars.add(bar);

                // Click handler (filtering).
                final int row = i;
                bar.addNodeMouseClickHandler(new NodeMouseClickHandler()
                {
                    @Override
                    public void onNodeMouseClick(NodeMouseClickEvent event)
                    {
                        BarChart.this.fireEvent(new ValueSelectedEvent(series.getName(), getData().getCategoryAxisProperty(), row));
                    }
                });

                // Mouse events for the bar shape.
                final int numValue = i;
                bar.addNodeMouseEnterHandler(new NodeMouseEnterHandler()
                {
                    @Override
                    public void onNodeMouseEnter(NodeMouseEnterEvent event)
                    {
                        double x = bar.getX();
                        double y = bar.getY();
                        double width = bar.getWidth();
                        double height = bar.getHeight();
                        double xTooltip = isVertical() ? x + width / 2 : x + width;
                        double yTooltip = isVertical() ? y - tooltip.getTailValue() : y + height / 2;
                        seriesValuesAlpha(numSeries, numValue, 0.5d);
                        tooltip.setLayer(bar.getViewport().getOverLayer());
                        tooltip.setValues(xValueFormatted, yValueFormatted);
                        tooltip.show(xTooltip, yTooltip);
                    }
                });

                bar.addNodeMouseExitHandler(new NodeMouseExitHandler()
                {
                    @Override
                    public void onNodeMouseExit(NodeMouseExitEvent event)
                    {
                        seriesValuesAlpha(numSeries, numValue, 1d);
                        tooltip.hide();
                    }
                });
                chartArea.add(bar);
            }
            seriesValues.put(series.getName(), bars);

        }
        return this;
    }

    protected void seriesValuesAlpha(int numSeries, int numValue, double alpha)
    {
        final String barId = BarChart.buildId("value", numSeries, numValue);
        for (Map.Entry<String, List<Rectangle>> entry : seriesValues.entrySet())
        {
            List<Rectangle> values = entry.getValue();
            if (values != null && !values.isEmpty())
            {
                for (Rectangle value : values)
                {
                    String id = value.getID();
                    if (!barId.equals(id))
                    {
                        value.animate(LINEAR, toPropertyList(ALPHA(alpha)), getDefaultAnimationDuration());
                    }
                }
            }
        }
    }

    public BarChart removeSeriesValues(final String seriesName)
    {
        if (seriesName != null)
        {
            seriesValues.remove(seriesName);
        }
        return this;
    }

    protected AxisBuilder<?> buildCategoryAxisBuilder(final boolean isVertical)
    {
        AxisBuilder<?> categoriesAxisBuilder = null;
        final Axis.AxisJSO categoriesAxisJSO = getCategoriesAxis();
        final AxisDirection direction = AxisDirection.ASC;

        if (isVertical)
        {
            if (Axis.getAxisTypeOf(categoriesAxisJSO) == AxisType.CATEGORY)
            {
                categoriesAxisBuilder = new CategoryAxisBuilder(getData(), getChartWidth(), direction, categoriesAxisJSO);
            }
            else if (Axis.getAxisTypeOf(categoriesAxisJSO) == AxisType.NUMBER)
            {
                categoriesAxisBuilder = new NumericAxisBuilder(getData(), getChartWidth(), direction, categoriesAxisJSO);
            }
            else
            {
                // TODO: categoriesAxisBuilder = new DateAxisBuilder(getData(), xAxisJSO);
            }
        }
        else
        {
            if (Axis.getAxisTypeOf(categoriesAxisJSO) == AxisType.CATEGORY)
            {
                categoriesAxisBuilder = new CategoryAxisBuilder(getData(), getChartHeight(), direction, categoriesAxisJSO);
            }
            else if (Axis.getAxisTypeOf(categoriesAxisJSO) == AxisType.NUMBER)
            {
                categoriesAxisBuilder = new NumericAxisBuilder(getData(), getChartHeight(), direction, categoriesAxisJSO);
            }
            else
            {
                // TODO: xAxisBuilder = new DateAxisBuilder(getData(), xAxisJSO);
            }
        }
        return categoriesAxisBuilder;
    }

    protected AxisBuilder<?> buildValuesAxisBuilder(final boolean isVertical)
    {
        AxisBuilder<?> valuesAxisBuilder = null;
        final Axis.AxisJSO valuesAxisJSO = getValuesAxis();

        if (isVertical)
        {
            final AxisDirection direction = isPositiveDirection(getDirection()) ? AxisDirection.DESC : AxisDirection.ASC;
            if (Axis.getAxisTypeOf(valuesAxisJSO) == AxisType.CATEGORY)
            {
                throw new RuntimeException("CategoryAxis type cannot be used in BarChart (vertical) for the values axis.");
            }
            else if (Axis.getAxisTypeOf(valuesAxisJSO) == AxisType.NUMBER)
            {
                valuesAxisBuilder = new NumericAxisBuilder(getData(), getChartHeight(), direction, valuesAxisJSO);
            }
            else
            {
                // TODO: valuesAxisBuilder = new DateAxisBuilder(getData(), yAxisJSO);
            }
        }
        else
        {
            final AxisDirection direction = isPositiveDirection(getDirection()) ? AxisDirection.ASC : AxisDirection.DESC;
            if (Axis.getAxisTypeOf(valuesAxisJSO) == AxisType.CATEGORY)
            {
                throw new RuntimeException("CategoryAxis type cannot be used in BarChart (horizontal) for the values axis.");
            }
            else if (Axis.getAxisTypeOf(valuesAxisJSO) == AxisType.NUMBER)
            {
                valuesAxisBuilder = new NumericAxisBuilder(getData(), getChartWidth(), direction, valuesAxisJSO);
            }
            else
            {
                // TODO: yAxisBuilder = new DateAxisBuilder(getData(), yAxisJSO);
            }
        }
        return valuesAxisBuilder;
    }

    public void clear()
    {
        super.clear();
        
        if (!seriesValues.isEmpty())
        {
            for (Collection<Rectangle> rectangles : seriesValues.values())
            {
                for (Rectangle rectangle : rectangles)
                {
                    rectangle.removeFromParent();
                }
            }
            seriesValues.clear();
        }
    }

    private static String buildId(String prefix, int numSeries, int numValue)
    {
        return prefix + numSeries + "" + numValue;
    }

    public Map<String, List<Rectangle>> getSeriesValues()
    {
        return seriesValues;
    }

}