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

package com.ait.lienzo.charts.client.core.xy;

import com.ait.lienzo.charts.client.core.AbstractChart;
import com.ait.lienzo.charts.client.core.ChartAttribute;
import com.ait.lienzo.charts.client.core.ChartNodeType;
import com.ait.lienzo.charts.client.core.axis.Axis;
import com.ait.lienzo.charts.client.core.legend.ChartLegend;
import com.ait.lienzo.charts.client.core.xy.axis.AxisBuilder;
import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.charts.client.core.xy.event.DataReloadedEvent;
import com.ait.lienzo.charts.client.core.xy.event.DataReloadedEventHandler;
import com.ait.lienzo.charts.client.core.xy.event.ValueSelectedEvent;
import com.ait.lienzo.charts.client.core.xy.event.ValueSelectedHandler;
import com.ait.lienzo.charts.client.core.xy.label.XYChartLabel;
import com.ait.lienzo.charts.client.core.xy.tooltip.XYChartTooltip;
import com.ait.lienzo.charts.shared.core.types.ChartDirection;
import com.ait.lienzo.charts.shared.core.types.ChartOrientation;
import com.ait.lienzo.charts.shared.core.types.LabelsPosition;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>Base XY chart implementation.</p>
 */
public abstract class XYChart<T extends XYChart<T>> extends AbstractChart<T>
{
    protected final List<Text>         categoriesAxisTitle = new LinkedList<Text>();

    protected final List<Text>         valuesAxisTitle     = new LinkedList<Text>();         ;

    protected final List<Line>         valuesAxisIntervals = new LinkedList<Line>();        // The lines that represents the intervals in the Y axis.

    protected final List<XYChartLabel> valuesLabels        = new LinkedList<XYChartLabel>(); // The texts that represents the interval values in the Y axis.

    protected final List<XYChartLabel> seriesLabels        = new LinkedList<XYChartLabel>(); // The labels for each interval (rectangle) in the X axis.

    // Axis builders.
    protected AxisBuilder<?>           categoriesAxisBuilder;

    protected AxisBuilder<?>           valuesAxisBuilder;

    protected XYChartTooltip           tooltip             = null;                          // The tooltip.

    protected XYChart(final JSONObject node, final ChartNodeType nodeType, final ValidationContext ctx) throws ValidationException
    {
        super(nodeType, node, ctx);
    }

    public XYChart(final ChartNodeType nodeType)
    {
        super(nodeType);
    }

    public final T setCategoriesAxis(Axis xAxis)
    {
        if (null != xAxis)
        {
            getAttributes().put(ChartAttribute.CATEGORIES_AXIS.getProperty(), xAxis.getJSO());
        }
        else
        {
            getAttributes().remove(ChartAttribute.CATEGORIES_AXIS.getProperty());
        }
        return cast();
    }

    public final Axis.AxisJSO getCategoriesAxis()
    {
        return getAttributes().getObject(ChartAttribute.CATEGORIES_AXIS.getProperty()).cast();
    }

    public final T setValuesAxis(Axis yAxis)
    {
        if (null != yAxis)
        {
            getAttributes().put(ChartAttribute.VALUES_AXIS.getProperty(), yAxis.getJSO());
        }
        else
        {
            getAttributes().remove(ChartAttribute.VALUES_AXIS.getProperty());
        }
        return cast();
    }

    public final Axis.AxisJSO getValuesAxis()
    {
        return getAttributes().getObject(ChartAttribute.VALUES_AXIS.getProperty()).cast();
    }

    public final T setData(final XYChartData data)
    {
        if (null != data)
        {
            getAttributes().put(ChartAttribute.XY_CHART_DATA.getProperty(), data.getJSO());
        }
        else
        {
            getAttributes().remove(ChartAttribute.XY_CHART_DATA.getProperty());
        }
        return cast();
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

    public final T setShowCategoriesAxisTitle(boolean showCategoriesAxisTitle)
    {
        getAttributes().put(ChartAttribute.SHOW_CATEGORIES_AXIS_TITLE.getProperty(), showCategoriesAxisTitle);

        return cast();
    }

    public final boolean isShowCategoriesAxisTitle()
    {
        if (getAttributes().isDefined(ChartAttribute.SHOW_CATEGORIES_AXIS_TITLE))
        {
            return getAttributes().getBoolean(ChartAttribute.SHOW_CATEGORIES_AXIS_TITLE.getProperty());
        }
        return true;
    }

    public final T setShowValuesAxisTitle(boolean showValuesAxisTitle)
    {
        getAttributes().put(ChartAttribute.SHOW_VALUES_AXIS_TITLE.getProperty(), showValuesAxisTitle);

        return cast();
    }

    public final boolean isShowValuesAxisTitle()
    {
        if (getAttributes().isDefined(ChartAttribute.SHOW_VALUES_AXIS_TITLE))
        {
            return getAttributes().getBoolean(ChartAttribute.SHOW_VALUES_AXIS_TITLE.getProperty());
        }
        return true;
    }

    public final T setCategoriesAxisLabelsPosition(LabelsPosition labelsPosition)
    {
        if (null != labelsPosition)
        {
            getAttributes().put(ChartAttribute.CATEGORIES_AXIS_LABELS_POSITION.getProperty(), labelsPosition.getValue());
        }
        else
        {
            getAttributes().remove(ChartAttribute.CATEGORIES_AXIS_LABELS_POSITION.getProperty());
        }
        return cast();
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

    public final T setValuesAxisLabelsPosition(LabelsPosition labelsPosition)
    {
        if (null != labelsPosition)
        {
            getAttributes().put(ChartAttribute.VALUES_AXIS_LABELS_POSITION.getProperty(), labelsPosition.getValue());
        }
        else
        {
            getAttributes().remove(ChartAttribute.VALUES_AXIS_LABELS_POSITION.getProperty());
        }
        return cast();
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

    public HandlerRegistration addDataReloadedHandler(DataReloadedEventHandler handler)
    {
        return addEnsureHandler(DataReloadedEvent.TYPE, handler);
    }

    public HandlerRegistration addValueSelectedHandler(ValueSelectedHandler handler)
    {
        return addEnsureHandler(ValueSelectedEvent.TYPE, handler);
    }

    public abstract static class XYChartFactory<T extends XYChart<T>> extends ChartFactory<T>
    {
        public XYChartFactory(final ChartNodeType nodeType)
        {
            super(nodeType);
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
            List<AxisLabel> xAxisLabels = categoriesAxisBuilder.getCategoriesAxisLabels();
            if (xAxisLabels != null)
            {
                for (int i = 0; i < xAxisLabels.size(); i++)
                {
                    AxisLabel axisLabel = xAxisLabels.get(i);
                    XYChartLabel label = new XYChartLabel(axisLabel);
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
        List<AxisLabel> yAxisLabels = valuesAxisBuilder.getValuesAxisLabels();
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
                XYChartLabel label = new XYChartLabel(yAxisLabel);
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

    protected abstract AxisBuilder<?> buildCategoryAxisBuilder(final boolean isVertical);

    protected abstract AxisBuilder<?> buildValuesAxisBuilder(final boolean isVertical);

    public abstract T buildSeriesValues(final XYChartSeries series, final int numSeries);

    @Override
    protected void buildTitle()
    {
        super.buildTitle();

        topArea.add(chartTitle);
    }

    private void buildTooltip()
    {
        tooltip = new XYChartTooltip();

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
            for (XYChartLabel label : valuesLabels)
            {
                label.removeFromParent();
            }
            valuesLabels.clear();
        }
        if (!seriesLabels.isEmpty())
        {
            for (XYChartLabel label : seriesLabels)
            {
                label.removeFromParent();
            }
            seriesLabels.clear();
        }

        categoriesAxisBuilder = null;
        valuesAxisBuilder = null;
        if (tooltip != null) tooltip.removeFromParent();
        super.clear();
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

    public List<XYChartLabel> getValuesLabels()
    {
        return valuesLabels;
    }

    public List<XYChartLabel> getSeriesLabels()
    {
        return seriesLabels;
    }

    public AxisBuilder<?> getCategoriesAxisBuilder()
    {
        return categoriesAxisBuilder;
    }

    public AxisBuilder<?> getValuesAxisBuilder()
    {
        return valuesAxisBuilder;
    }

    public XYChartTooltip getChartTooltip()
    {
        return tooltip;
    }
}