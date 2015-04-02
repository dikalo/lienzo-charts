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

package com.ait.lienzo.charts.client.core.xy.line;

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
import com.ait.lienzo.charts.client.core.xy.event.ValueSelectedEvent;
import com.ait.lienzo.charts.client.core.xy.line.animation.LineChartResizeAnimation;
import com.ait.lienzo.charts.shared.core.types.AxisDirection;
import com.ait.lienzo.charts.shared.core.types.AxisType;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.json.client.JSONObject;

import java.util.*;

import static com.ait.lienzo.client.core.animation.AnimationProperties.toPropertyList;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.ALPHA;
import static com.ait.lienzo.client.core.animation.AnimationTweener.LINEAR;

/**
 * <p>XY chart implementation using Line as shapes for values.</p>
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
 *     <li><code>ORIENTATION</code>: The chart orientation (Vertical or Horizontal).</li>
 * </ul>
 * 
 */
public class LineChart extends XYChart<LineChart>
{
    private final static double     LINE_STROKE_WIDTH = 5d;

    private final static double     CIRCLE_RADIUS     = 10d;

    final Map<String, List<Circle>> seriesPoints      = new LinkedHashMap<String, List<Circle>>(); // The circle instances that represents the exact data point for axis type category.

    final Map<String, List<Line>>   seriesValues      = new LinkedHashMap<String, List<Line>>();  // The Line instances that represents the data.

    protected LineChart(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(node, ChartNodeType.LINE_CHART, ctx);
    }

    public LineChart()
    {
        super(ChartNodeType.LINE_CHART);

        getMetaData().put("creator", "Roger Martinez");
    }

    @Override
    public LineChart init()
    {
        return LineChartAnimationHelper.create(this, AnimationTweener.LINEAR, getDefaultAnimationDuration());
    }

    @Override
    public LineChart init(double duration)
    {
        return LineChartAnimationHelper.create(this, AnimationTweener.LINEAR, Math.max(duration, 1));
    }

    @Override
    public LineChart init(AnimationTweener tweener, double duration)
    {
        return LineChartAnimationHelper.create(this, ((tweener != null) ? tweener : AnimationTweener.LINEAR), Math.max(duration, 1));
    }

    public LineChart reload(XYChartData data)
    {
        return LineChartAnimationHelper.reload(this, data, LINEAR, getDefaultAnimationDuration());
    }

    public LineChart reload(XYChartData data, double duration)
    {
        return LineChartAnimationHelper.reload(this, data, LINEAR, Math.max(duration, 1));
    }

    public LineChart reload(XYChartData data, AnimationTweener tweener, double duration)
    {
        return LineChartAnimationHelper.reload(this, data, ((tweener != null) ? tweener : LINEAR), Math.max(duration, 1));
    }

    public static class LineChartFactory extends XYChartFactory<LineChart>
    {
        public LineChartFactory()
        {
            super(ChartNodeType.LINE_CHART);
        }

        @Override
        public boolean addNodeForContainer(IContainer<?, ?> container, Node<?> node, ValidationContext ctx)
        {
            return false;
        }

        @Override
        protected LineChart container(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new LineChart(node, ctx);
        }
    }

    @Override
    protected void onChartResize(ChartResizeEvent event)
    {
        final boolean apply = event.isApply();

        if (apply)
        {
            final double w = event.getWidth() - getMarginLeft() - getMarginRight();
            final double h = event.getHeight() - getMarginTop() - getMarginBottom();
            // Apply resize to line chart.
            new LineChartResizeAnimation(this, w, h, LINEAR, getDefaultAnimationDuration(), null).run();
        }
        super.onChartResize(event);
    }

    public LineChart buildSeriesValues(final XYChartSeries series, final int numSeries)
    {
        List<? extends AxisValue<?>> xAxisValues = categoriesAxisBuilder.getValues(getData().getCategoryAxisProperty());
        List<? extends AxisValue<?>> yAxisValues = valuesAxisBuilder.getValues(series.getValuesAxisProperty());

        if (xAxisValues != null)
        {
            final List<Line> lines = new LinkedList<Line>();
            final List<Circle> circles = new LinkedList<Circle>();
            final List<Point2D> points = new LinkedList<Point2D>();
            for (int i = 0; i < xAxisValues.size(); i++)
            {
                final AxisValue<?> axisValue = xAxisValues.get(i);
                final AxisValue<?> yAxisValue = yAxisValues.get(i);
                final Object value = axisValue.getValue();
                final Object yValue = yAxisValue.getValue();
                final String xValueFormatted = categoriesAxisBuilder.format(value);
                final String yValueFormatted = valuesAxisBuilder.format(yValue);
                final Point2D point = new Point2D(0, 0);
                points.add(point);

                // Build the circle that shows exact point.
                final Circle circle = new Circle(CIRCLE_RADIUS);
                circles.add(circle);
                chartArea.add(circle);

                // Click handler (filtering).
                final int row = i;
                circle.addNodeMouseClickHandler(new NodeMouseClickHandler()
                {
                    @Override
                    public void onNodeMouseClick(NodeMouseClickEvent event)
                    {
                        LineChart.this.fireEvent(new ValueSelectedEvent(series.getName(), getData().getCategoryAxisProperty(), row));
                    }
                });

                // Mouse events for the line shape.
                final int numValue = i;
                circle.addNodeMouseEnterHandler(new NodeMouseEnterHandler()
                {
                    @Override
                    public void onNodeMouseEnter(NodeMouseEnterEvent event)
                    {
                        double x = circle.getX();
                        double y = circle.getY();
                        seriesValuesAlpha(numSeries, numValue, 0.5d);
                        tooltip.setLayer(circle.getViewport().getOverLayer());
                        tooltip.setValues(xValueFormatted, yValueFormatted);
                        tooltip.show(x, y);
                    }
                });

                circle.addNodeMouseExitHandler(new NodeMouseExitHandler()
                {
                    @Override
                    public void onNodeMouseExit(NodeMouseExitEvent event)
                    {
                        seriesValuesAlpha(numSeries, numValue, 1d);
                        tooltip.hide();
                    }
                });

                // Build lines between value positions.
                if (i > 0)
                {
                    final Point2D lastPoint = points.get(i - 1);

                    final Line line = new Line(lastPoint, point).setStrokeWidth(LINE_STROKE_WIDTH);
                    lines.add(line);
                    chartArea.add(line);
                }
            }
            seriesValues.put(series.getName(), lines);
            seriesPoints.put(series.getName(), circles);

        }
        return this;
    }

    protected void seriesValuesAlpha(int numSeries, int numValue, double alpha)
    {
        final String lineId = buildId("value", numSeries, numValue);
        for (Map.Entry<String, List<Circle>> entry : seriesPoints.entrySet())
        {
            List<Circle> values = entry.getValue();
            if (values != null && !values.isEmpty())
            {
                for (Circle value : values)
                {
                    String id = value.getID();
                    if (!lineId.equals(id))
                    {
                        value.animate(LINEAR, toPropertyList(ALPHA(alpha)), getDefaultAnimationDuration());
                    }
                }
            }
        }
    }

    public LineChart removeSeriesValues(final String seriesName)
    {
        if (seriesName != null)
        {
            seriesValues.remove(seriesName);
            seriesPoints.remove(seriesName);
        }
        return this;
    }

    private static String buildId(String prefix, int numSeries, int numValue)
    {
        return prefix + numSeries + "" + numValue;
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
                throw new RuntimeException("CategoryAxis type cannot be used in LineChart for the values axis.");
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
                throw new RuntimeException("CategoryAxis type cannot be used in LineChart for the values axis.");
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
            for (Collection<Line> lines : seriesValues.values())
            {
                for (Line line : lines)
                {
                    line.removeFromParent();
                }
            }
            seriesValues.clear();
        }

        if (!seriesPoints.isEmpty())
        {
            for (Collection<Circle> circles : seriesPoints.values())
            {
                for (Circle circle : circles)
                {
                    circle.removeFromParent();
                }
            }
            seriesPoints.clear();
        }
    }

    public Map<String, List<Line>> getSeriesValues()
    {
        return seriesValues;
    }

    public Map<String, List<Circle>> getSeriesPoints()
    {
        return seriesPoints;
    }

}