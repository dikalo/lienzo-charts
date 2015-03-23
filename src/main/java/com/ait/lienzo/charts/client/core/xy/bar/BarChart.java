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

package com.ait.lienzo.charts.client.core.xy.bar;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ait.lienzo.charts.client.core.AbstractChart;
import com.ait.lienzo.charts.client.core.ChartAttribute;
import com.ait.lienzo.charts.client.core.ChartNodeType;
import com.ait.lienzo.charts.client.core.axis.Axis;
import com.ait.lienzo.charts.client.core.axis.CategoryAxis;
import com.ait.lienzo.charts.client.core.legend.ChartLegend;
import com.ait.lienzo.charts.client.core.resizer.ChartResizeEvent;
import com.ait.lienzo.charts.client.core.xy.XYChartData;
import com.ait.lienzo.charts.client.core.xy.XYChartSeries;
import com.ait.lienzo.charts.client.core.xy.axis.AxisBuilder;
import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.charts.client.core.xy.axis.AxisValue;
import com.ait.lienzo.charts.client.core.xy.axis.CategoryAxisBuilder;
import com.ait.lienzo.charts.client.core.xy.axis.NumericAxisBuilder;
import com.ait.lienzo.charts.client.core.xy.bar.animation.BarChartResizeAnimation;
import com.ait.lienzo.charts.client.core.xy.bar.event.DataReloadedEvent;
import com.ait.lienzo.charts.client.core.xy.bar.event.DataReloadedEventHandler;
import com.ait.lienzo.charts.client.core.xy.bar.event.ValueSelectedEvent;
import com.ait.lienzo.charts.client.core.xy.bar.event.ValueSelectedHandler;
import com.ait.lienzo.charts.shared.core.types.AxisDirection;
import com.ait.lienzo.charts.shared.core.types.AxisType;
import com.ait.lienzo.charts.shared.core.types.ChartDirection;
import com.ait.lienzo.charts.shared.core.types.ChartOrientation;
import com.ait.lienzo.charts.shared.core.types.LabelsPosition;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;

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
public class BarChart extends AbstractChart<BarChart>
{
    // Default separation size between bars.
    public static final double         BAR_SEPARATION          = 2;

    public static final double         BAR_MAX_SIZE            = 75;

    // If bar are too big, use this proportion (30%).
    public static final double         BAR_MAX_SIZE_PROPORTION = 0.3;

    final List<Text>                   categoriesAxisTitle     = new LinkedList<Text>();

    final List<Text>                   valuesAxisTitle         = new LinkedList<Text>();                       ;

    final List<Line>                   valuesAxisIntervals     = new LinkedList<Line>();                      // The lines that represents the intervals in the Y axis.

    final List<BarChartLabel>          valuesLabels            = new LinkedList<BarChartLabel>();             // The texts that represents the interval values in the Y axis.

    final List<BarChartLabel>          seriesLabels            = new LinkedList<BarChartLabel>();             // The labels for each interval (rectangle) in the X axis.

    final Map<String, List<Rectangle>> seriesValues            = new LinkedHashMap<String, List<Rectangle>>(); // The rectangles that represents the data.

    // Axis builders.
    AxisBuilder                        categoriesAxisBuilder;

    AxisBuilder                        valuesAxisBuilder;

    BarChartTooltip                    tooltip                 = null;                                        // The tooltip.

    protected BarChart(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ChartNodeType.BAR_CHART, node, ctx);
    }

    public BarChart()
    {
        super(ChartNodeType.BAR_CHART);

        getMetaData().put("creator", "Roger Martinez");
    }

    @Override
    public BarChart init()
    {
        BarChartAnimationHelper.create(this, AnimationTweener.LINEAR, 1, null);

        return this;
    }

    @Override
    public BarChart init(double duration)
    {
        BarChartAnimationHelper.create(this, AnimationTweener.LINEAR, duration, null);

        return this;
    }

    @Override
    public BarChart init(AnimationTweener tweener, double duration)
    {
        BarChartAnimationHelper.create(this, AnimationTweener.LINEAR, duration, null);

        return this;
    }

    public BarChart reload(XYChartData data)
    {
        BarChartAnimationHelper.reload(this, data, AnimationTweener.LINEAR, 1, null);

        return this;
    }

    public BarChart reload(XYChartData data, double duration)
    {
        BarChartAnimationHelper.reload(this, data, AnimationTweener.LINEAR, duration, null);

        return this;
    }

    public BarChart reload(XYChartData data, AnimationTweener tweener, double duration)
    {
        BarChartAnimationHelper.reload(this, data, tweener, duration, null);

        return this;
    }

    public final BarChart setCategoriesAxis(CategoryAxis xAxis)
    {
        if (null != xAxis)
        {
            getAttributes().put(ChartAttribute.CATEGORIES_AXIS.getProperty(), xAxis.getJSO());
        }
        else
        {
            getAttributes().delete(ChartAttribute.CATEGORIES_AXIS.getProperty());
        }
        return this;
    }

    public final Axis.AxisJSO getCategoriesAxis()
    {
        return getAttributes().getObject(ChartAttribute.CATEGORIES_AXIS.getProperty()).cast();
    }

    public final BarChart setValuesAxis(Axis yAxis)
    {
        if (null != yAxis)
        {
            getAttributes().put(ChartAttribute.VALUES_AXIS.getProperty(), yAxis.getJSO());
        }
        else
        {
            getAttributes().delete(ChartAttribute.VALUES_AXIS.getProperty());
        }
        return this;
    }

    public final Axis.AxisJSO getValuesAxis()
    {
        return getAttributes().getObject(ChartAttribute.VALUES_AXIS.getProperty()).cast();
    }

    public final BarChart setData(final XYChartData data)
    {
        if (null != data)
        {
            getAttributes().put(ChartAttribute.XY_CHART_DATA.getProperty(), data.getJSO());
        }
        else
        {
            getAttributes().delete(ChartAttribute.XY_CHART_DATA.getProperty());
        }
        return this;
    }

    public final XYChartData getData()
    {
        if (getAttributes().isDefined(ChartAttribute.XY_CHART_DATA))
        {
            XYChartData.XYChartDataJSO jso = getAttributes().getArrayOfJSO(ChartAttribute.XY_CHART_DATA.getProperty()).cast();
            return new XYChartData(jso);
        }
        return null;
    }

    public final BarChart setShowCategoriesAxisTitle(boolean showCategoriesAxisTitle)
    {
        getAttributes().put(ChartAttribute.SHOW_CATEGORIES_AXIS_TITLE.getProperty(), showCategoriesAxisTitle);

        return this;
    }

    public final boolean isShowCategoriesAxisTitle()
    {
        if (getAttributes().isDefined(ChartAttribute.SHOW_CATEGORIES_AXIS_TITLE))
        {
            return getAttributes().getBoolean(ChartAttribute.SHOW_CATEGORIES_AXIS_TITLE.getProperty());
        }
        return true;
    }

    public final BarChart setShowValuesAxisTitle(boolean showValuesAxisTitle)
    {
        getAttributes().put(ChartAttribute.SHOW_VALUES_AXIS_TITLE.getProperty(), showValuesAxisTitle);

        return this;
    }

    public final boolean isShowValuesAxisTitle()
    {
        if (getAttributes().isDefined(ChartAttribute.SHOW_VALUES_AXIS_TITLE))
        {
            return getAttributes().getBoolean(ChartAttribute.SHOW_VALUES_AXIS_TITLE.getProperty());
        }
        return true;
    }

    public final BarChart setCategoriesAxisLabelsPosition(LabelsPosition labelsPosition)
    {
        if (null != labelsPosition)
        {
            getAttributes().put(ChartAttribute.CATEGORIES_AXIS_LABELS_POSITION.getProperty(), labelsPosition.getValue());
        }
        else
        {
            getAttributes().delete(ChartAttribute.CATEGORIES_AXIS_LABELS_POSITION.getProperty());
        }
        return this;
    }

    public final LabelsPosition getCategoriesAxisLabelsPosition()
    {
        LabelsPosition position = LabelsPosition.lookup(getAttributes().getString(ChartAttribute.CATEGORIES_AXIS_LABELS_POSITION.getProperty()));

        // Check valid values, if not, return default one.
        if (!LabelsPosition.NONE.equals(position) && isVertical())
        {
            if (LabelsPosition.LEFT.equals(position) || LabelsPosition.RIGHT.equals(position)) position = LabelsPosition.BOTTOM;
        }
        else
        {
            if (LabelsPosition.TOP.equals(position) || LabelsPosition.BOTTOM.equals(position)) position = LabelsPosition.LEFT;
        }

        return position;
    }

    public final BarChart setValuesAxisLabelsPosition(LabelsPosition labelsPosition)
    {
        if (null != labelsPosition)
        {
            getAttributes().put(ChartAttribute.VALUES_AXIS_LABELS_POSITION.getProperty(), labelsPosition.getValue());
        }
        else
        {
            getAttributes().delete(ChartAttribute.VALUES_AXIS_LABELS_POSITION.getProperty());
        }
        return this;
    }

    public final LabelsPosition getValuesAxisLabelsPosition()
    {
        LabelsPosition position = LabelsPosition.lookup(getAttributes().getString(ChartAttribute.VALUES_AXIS_LABELS_POSITION.getProperty()));

        // Check valid values, if not, return default one.
        if (!LabelsPosition.NONE.equals(position) && isVertical())
        {
            if (LabelsPosition.BOTTOM.equals(position) || LabelsPosition.TOP.equals(position)) position = LabelsPosition.RIGHT;
        }
        else
        {
            if (LabelsPosition.RIGHT.equals(position) || LabelsPosition.LEFT.equals(position)) position = LabelsPosition.BOTTOM;
        }

        return position;

    }

    public static class BarChartFactory extends ChartFactory<BarChart>
    {
        public BarChartFactory()
        {
            super(ChartNodeType.BAR_CHART);
            addAttribute(ChartAttribute.XY_CHART_DATA, true);
            addAttribute(ChartAttribute.CATEGORIES_AXIS, true);
            addAttribute(ChartAttribute.VALUES_AXIS, true);
            addAttribute(ChartAttribute.SHOW_CATEGORIES_AXIS_TITLE, false);
            addAttribute(ChartAttribute.SHOW_VALUES_AXIS_TITLE, false);
            addAttribute(ChartAttribute.CATEGORIES_AXIS_LABELS_POSITION, false);
            addAttribute(ChartAttribute.VALUES_AXIS_LABELS_POSITION, false);
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

    public boolean isVertical()
    {
        return ChartOrientation.VERTICAL.equals(getOrientation());
    }

    protected static boolean isPositiveDirection(ChartDirection direction)
    {
        return ChartDirection.POSITIVE.equals(direction);
    }

    public boolean isShowCategoriesLabels()
    {
        return !LabelsPosition.NONE.equals(getCategoriesAxisLabelsPosition());
    }

    public boolean isShowValuesLabels()
    {
        return !LabelsPosition.NONE.equals(getValuesAxisLabelsPosition());
    }

    public HandlerRegistration addDataReloadedHandler(DataReloadedEventHandler handler)
    {
        return addEnsureHandler(DataReloadedEvent.TYPE, handler);
    }

    public HandlerRegistration addValueSelectedHandler(ValueSelectedHandler handler)
    {
        return addEnsureHandler(ValueSelectedEvent.TYPE, handler);
    }

    protected void doDraw()
    {
        if (getData() == null)
        {
            GWT.log("No data");
            return;
        }
        // Orientation.
        final boolean isVertical = isVertical();
        categoriesAxisBuilder = buildCategoryAxisBuilder(isVertical);
        valuesAxisBuilder = buildValuesAxisBuilder(isVertical);

        // Category axis title.
        if (isShowCategoriesAxisTitle())
        {
            final double x = isVertical ? getChartWidth() / 2 : 10;
            final double y = isVertical ? 30 : getChartHeight() / 2;
            final TextAlign textAlign = isVertical ? TextAlign.CENTER : TextAlign.RIGHT;
            final double rotation = isVertical ? 0 : 270;
            Text categoriesAxisTitle = new Text(getCategoriesAxis().getTitle(), getFontFamily(), getFontStyle(), getFontSize()).setFillColor(ColorName.SILVER).setX(x).setY(y).setTextAlign(textAlign).setTextBaseLine(TextBaseLine.MIDDLE).setRotationDegrees(rotation);
            if (isVertical) bottomArea.add(categoriesAxisTitle);
            else leftArea.add(categoriesAxisTitle);
            this.categoriesAxisTitle.add(categoriesAxisTitle);
        }
        // Values axis title.
        if (isShowValuesAxisTitle())
        {
            final double x = isVertical ? 10 : getChartWidth() / 2;
            final double y = isVertical ? getChartHeight() / 2 : 30;
            final TextAlign textAlign = isVertical ? TextAlign.RIGHT : TextAlign.CENTER;
            final double rotation = isVertical ? 270 : 0;
            Text valuesAxisTitle = new Text(getValuesAxis().getTitle(), getFontFamily(), getFontStyle(), getFontSize()).setFillColor(ColorName.SILVER).setX(x).setY(y).setTextAlign(textAlign).setTextBaseLine(TextBaseLine.MIDDLE).setRotationDegrees(rotation);
            if (isVertical) leftArea.add(valuesAxisTitle);
            else bottomArea.add(valuesAxisTitle);
            this.valuesAxisTitle.add(valuesAxisTitle);
        }
        // Categories axis intervals.
        if (isShowCategoriesLabels())
        {
            List<AxisLabel> xAxisLabels = categoriesAxisBuilder.getLabels();
            if (xAxisLabels != null)
            {
                for (int i = 0; i < xAxisLabels.size(); i++)
                {
                    AxisLabel axisLabel = xAxisLabels.get(i);
                    BarChartLabel label = new BarChartLabel(axisLabel);
                    seriesLabels.add(label);
                    if (isVertical)
                    {
                        if (getCategoriesAxisLabelsPosition().equals(LabelsPosition.BOTTOM)) bottomArea.add(label);
                        else topArea.add(label);
                    }
                    else
                    {
                        if (getCategoriesAxisLabelsPosition().equals(LabelsPosition.LEFT)) leftArea.add(label);
                        else rightArea.add(label);
                    }
                }
            }

        }
        // Values axis intervals.
        // Build the shapes axis instances (line for intervals and text for labels).
        List<AxisLabel> yAxisLabels = valuesAxisBuilder.getLabels();
        for (AxisLabel yAxisLabel : yAxisLabels)
        {
            final double p00 = isVertical() ? 0 : 0;
            final double p01 = isVertical() ? getChartHeight() : 0;
            final double p10 = isVertical() ? getChartWidth() : 0;
            final double p11 = isVertical() ? getChartHeight() : getChartHeight();
            Line valuesAxisIntervals = new Line(p00, p01, p10, p11).setStrokeColor(ColorName.DARKGREY);
            chartArea.add(valuesAxisIntervals);
            if (isShowValuesLabels())
            {
                BarChartLabel label = new BarChartLabel(yAxisLabel);
                valuesLabels.add(label);
                if (isVertical)
                {
                    if (getValuesAxisLabelsPosition().equals(LabelsPosition.LEFT)) leftArea.add(label);
                    else rightArea.add(label);
                }
                else
                {
                    if (getValuesAxisLabelsPosition().equals(LabelsPosition.TOP)) topArea.add(label);
                    else bottomArea.add(label);
                }
            }
            this.valuesAxisIntervals.add(valuesAxisIntervals);
        }
        // Build the chart values as rectangle shapes.
        XYChartSeries[] series = getData().getSeries();
        for (int numSeries = 0; numSeries < series.length; numSeries++)
        {
            final XYChartSeries _series = series[numSeries];
            buildSeriesValues(_series, numSeries);
        }
        // Tooltip.
        buildTooltip();

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
            new BarChartResizeAnimation(this, w, h, AnimationTweener.LINEAR, ANIMATION_DURATION, null).run();
        }
        super.onChartResize(event);
    }

    @Override
    protected void buildTitle()
    {
        super.buildTitle();

        topArea.add(chartTitle);
    }

    private void buildTooltip()
    {
        tooltip = new BarChartTooltip();

        chartArea.add(tooltip);
    }

    @Override
    protected void buildLegend()
    {
        super.buildLegend();

        // Add legend elements.
        XYChartSeries[] series = getData().getSeries();
        if (legend != null && series != null && series.length > 0)
        {
            for (XYChartSeries _series : series)
            {
                legend.add(new ChartLegend.ChartLegendEntry(_series.getName(), _series.getColor()));
            }
            legend.build();
        }
    }

    public BarChart buildSeriesValues(final XYChartSeries series, final int numSeries)
    {
        List<AxisValue> xAxisValues = categoriesAxisBuilder.getValues(getData().getCategoryAxisProperty());
        List<AxisValue> yAxisValues = valuesAxisBuilder.getValues(series.getValuesAxisProperty());

        if (xAxisValues != null)
        {
            List<Rectangle> bars = new LinkedList();
            for (int i = 0; i < xAxisValues.size(); i++)
            {
                final AxisValue axisValue = xAxisValues.get(i);
                AxisValue yAxisValue = yAxisValues.get(i);
                Object value = axisValue.getValue();
                Object yValue = yAxisValue.getValue();
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
                        double yTooltip = isVertical() ? y - BarChartTooltip.TRIANGLE_SIZE : y + height / 2;
                        seriesValuesAlpha(numSeries, numValue, 0.5d);
                        tooltip.setX(xTooltip).setY(yTooltip);
                        tooltip.show(xValueFormatted, yValueFormatted);
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
        String barId = BarChart.buildId("value", numSeries, numValue);
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
                        AnimationProperties animationProperties = new AnimationProperties();
                        animationProperties.push(AnimationProperty.Properties.ALPHA(alpha));
                        value.animate(AnimationTweener.LINEAR, animationProperties, ANIMATION_DURATION);
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

    private AxisBuilder buildCategoryAxisBuilder(boolean isVertical)
    {
        AxisBuilder categoriesAxisBuilder = null;
        Axis.AxisJSO categoriesAxisJSO = getCategoriesAxis();

        if (isVertical)
        {
            AxisDirection direction = isPositiveDirection(getDirection()) ? AxisDirection.DESC : AxisDirection.ASC;
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
            AxisDirection direction = isPositiveDirection(getDirection()) ? AxisDirection.ASC : AxisDirection.DESC;
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

    private AxisBuilder buildValuesAxisBuilder(boolean isVertical)
    {
        AxisBuilder valuesAxisBuilder = null;
        Axis.AxisJSO valuesAxisJSO = getValuesAxis();

        if (isVertical)
        {
            AxisDirection direction = isPositiveDirection(getDirection()) ? AxisDirection.DESC : AxisDirection.ASC;
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
            AxisDirection direction = isPositiveDirection(getDirection()) ? AxisDirection.ASC : AxisDirection.DESC;

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
        if (!categoriesAxisTitle.isEmpty())
        {
            categoriesAxisTitle.get(0).removeFromParent();
            categoriesAxisTitle.clear();

        }
        if (!valuesAxisTitle.isEmpty())
        {
            valuesAxisTitle.get(0).removeFromParent();
            valuesAxisTitle.clear();
        }
        if (!valuesAxisIntervals.isEmpty())
        {
            for (Line line : valuesAxisIntervals)
                line.removeFromParent();
            valuesAxisIntervals.clear();

        }
        if (!valuesLabels.isEmpty())
        {
            for (BarChartLabel label : valuesLabels)
            {
                label.removeFromParent();
            }
            valuesLabels.clear();
        }
        if (!seriesLabels.isEmpty())
        {
            for (BarChartLabel label : seriesLabels)
            {
                label.removeFromParent();
            }
            seriesLabels.clear();
        }
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
        categoriesAxisBuilder = null;
        valuesAxisBuilder = null;
        if (tooltip != null) tooltip.removeFromParent();
        super.clear();
    }

    public static String buildId(String prefix, int numSeries, int numValue)
    {
        return prefix + numSeries + "" + numValue;
    }

    public List<Text> getCategoriesAxisTitle()
    {
        return categoriesAxisTitle;
    }

    public List<Text> getValuesAxisTitle()
    {
        return valuesAxisTitle;
    }

    public List<Line> getValuesAxisIntervals()
    {
        return valuesAxisIntervals;
    }

    public List<BarChartLabel> getValuesLabels()
    {
        return valuesLabels;
    }

    public List<BarChartLabel> getSeriesLabels()
    {
        return seriesLabels;
    }

    public Map<String, List<Rectangle>> getSeriesValues()
    {
        return seriesValues;
    }

    public AxisBuilder getCategoriesAxisBuilder()
    {
        return categoriesAxisBuilder;
    }

    public AxisBuilder getValuesAxisBuilder()
    {
        return valuesAxisBuilder;
    }

    public BarChartTooltip getChartTooltip()
    {
        return tooltip;
    }
}