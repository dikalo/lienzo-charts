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
 */

package com.ait.lienzo.charts.client.core.pie;

import java.util.LinkedList;
import java.util.List;

import com.ait.lienzo.charts.client.core.AbstractChart;
import com.ait.lienzo.charts.client.core.ChartAttribute;
import com.ait.lienzo.charts.client.core.ChartNodeType;
import com.ait.lienzo.charts.client.core.legend.ChartLegend;
import com.ait.lienzo.charts.client.core.model.DataTable;
import com.ait.lienzo.charts.client.core.model.PieChartData;
import com.ait.lienzo.charts.client.core.pie.animation.PieChartClearAnimation;
import com.ait.lienzo.charts.client.core.pie.animation.PieChartResizeAnimation;
import com.ait.lienzo.charts.client.core.pie.event.DataReloadedEvent;
import com.ait.lienzo.charts.client.core.pie.event.DataReloadedEventHandler;
import com.ait.lienzo.charts.client.core.pie.event.ValueSelectedEvent;
import com.ait.lienzo.charts.client.core.pie.event.ValueSelectedHandler;
import com.ait.lienzo.charts.client.core.resizer.ChartResizeEvent;
import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Slice;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;

public class PieChart extends AbstractChart<PieChart>
{
    private Group                    slices;

    private Group                    labels;

    private List<Text>               texts                = new LinkedList<Text>();

    private List<PieSlice>           pieSlices            = new LinkedList<PieSlice>();

    private PieChartTooltip          tooltip;

    private static final ColorName[] DEFAULT_SLICE_COLORS = new ColorName[] { ColorName.DEEPPINK, ColorName.YELLOW, ColorName.SALMON, ColorName.CORNFLOWERBLUE, ColorName.AQUA, ColorName.DEEPSKYBLUE, ColorName.GREENYELLOW, ColorName.BLUEVIOLET, ColorName.FUCHSIA, ColorName.MAGENTA, ColorName.MAROON };

    protected PieChart(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ChartNodeType.PIE_CHART, node, ctx);
    }

    public PieChart()
    {
        super(ChartNodeType.PIE_CHART);
    }

    public HandlerRegistration addDataReloadedHandler(DataReloadedEventHandler handler)
    {
        return addEnsureHandler(DataReloadedEvent.TYPE, handler);
    }

    public HandlerRegistration addValueSelectedHandler(ValueSelectedHandler handler)
    {
        return addEnsureHandler(ValueSelectedEvent.TYPE, handler);
    }

    @Override
    public void doDraw()
    {
        PieChartData data = getData();

        if (getRadius(getChartWidth(), getChartHeight()) <= 0 || (null == data) || (data.size() < 1))
        {
            return;
        }
        slices = new Group();

        labels = new Group();

        _build(data);

        // Tooltip.
        buildTooltip();
    }

    @Override
    protected void moveAreas(double x, double y)
    {
        super.moveAreas(x, y);

        // Center slices and texts.
        if (slices != null) slices.setX(getChartWidth() / 2).setY(getChartHeight() / 2);
        if (labels != null) labels.setX(getChartWidth() / 2).setY(getChartHeight() / 2);
    }

    private void _build(PieChartData data)
    {
        final DataTable dataTable = data.getDataTable();
        final String[] categories = dataTable.getColumn(getData().getCategoriesProperty()).getStringValues();
        final Double[] values = dataTable.getColumn(getData().getValuesProperty()).getNumericValues();

        labels.setListening(false);

        //double sofar = 0;

        double total = 0;

        for (int i = 0; i < values.length; i++)
        {
            total += values[i];
        }
        for (int i = 0; i < values.length; i++)
        {
            final double value = values[i] / total;

            final PieSlice slice = new PieSlice(0, 0, 0);

            final int index = i;

            slice.addNodeMouseClickHandler(new NodeMouseClickHandler()
            {
                @Override
                public void onNodeMouseClick(NodeMouseClickEvent event)
                {
                    // GWT.log("PieChart - filtering on "  + categories[index] + "/" + index);
                    PieChart.this.fireEvent(new ValueSelectedEvent(getData().getCategoriesProperty(), index));
                }
            });
            final int _i = i;
            final String category = categories[i];
            slice.addNodeMouseEnterHandler(new NodeMouseEnterHandler()
            {
                @Override
                public void onNodeMouseEnter(NodeMouseEnterEvent event)
                {
                    // Animate other slices.
                    alphaToOtherSlices(slice.getID(), 0.5);

                    // Show the tooltip.
                    tooltip.setValues(category, getLabel(value * 100));
                    tooltip.show(getChartWidth() / 2, getChartHeight() / 2);

                    // Hide text.
                    AnimationProperties animationProperties = new AnimationProperties();
                    animationProperties.push(AnimationProperty.Properties.ALPHA(0));
                    Text _text = texts.get(_i);
                    if (_text != null) _text.animate(AnimationTweener.LINEAR, animationProperties, ANIMATION_DURATION);
                }
            });
            slice.addNodeMouseExitHandler(new NodeMouseExitHandler()
            {
                @Override
                public void onNodeMouseExit(NodeMouseExitEvent event)
                {
                    // Animate other slices.
                    alphaToOtherSlices(slice.getID(), 1);

                    // Hide tooltip.
                    if (tooltip != null) tooltip.hide();

                    // Show text.
                    AnimationProperties animationProperties = new AnimationProperties();
                    animationProperties.push(AnimationProperty.Properties.ALPHA(1));
                    Text _text = texts.get(_i);
                    if (_text != null) _text.animate(AnimationTweener.LINEAR, animationProperties, ANIMATION_DURATION);
                }
            });
            slice.setFillColor(getColor(i)).setStrokeColor(ColorName.BLACK).setStrokeWidth(1);
            slice.setID("pieSlice" + i);
            pieSlices.add(slice);
            slices.add(slice);

            Text text = new Text(getLabel(value * 100), getFontFamily(), getFontStyle(), getFontSize()).setFillColor(ColorName.BLACK).setTextBaseLine(TextBaseLine.MIDDLE).setAlpha(0d);
            texts.add(text);

            labels.add(text);
        }
        addOnAreaChartCentered(labels);

        addOnAreaChartCentered(slices);
    }

    public Group getSlices()
    {
        return slices;
    }

    public Group getLabels()
    {
        return labels;
    }

    public List<Text> getTexts()
    {
        return texts;
    }

    public List<PieSlice> getPieSlices()
    {
        return pieSlices;
    }

    public PieChartTooltip getTooltip()
    {
        return tooltip;
    }

    @Override
    public void clear()
    {
        texts.clear();
        labels.removeFromParent();
        pieSlices.clear();
        slices.removeFromParent();
        if (tooltip != null) tooltip.removeFromParent();
        super.clear();
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
            new PieChartResizeAnimation(this, w, h, AnimationTweener.LINEAR, ANIMATION_DURATION, null).run();
        }
        super.onChartResize(event);
    }

    protected void alphaToOtherSlices(String sliceID, double alpha)
    {
        for (PieSlice slice : pieSlices)
        {
            if (!slice.getID().equals(sliceID))
            {
                AnimationProperties animationProperties = new AnimationProperties();
                animationProperties.push(AnimationProperty.Properties.ALPHA(alpha));
                slice.animate(AnimationTweener.LINEAR, animationProperties, ANIMATION_DURATION);
            }
        }
    }

    @Override
    protected void buildTitle()
    {
        super.buildTitle();

        topArea.add(chartTitle);
    }

    @Override
    protected void buildLegend()
    {
        super.buildLegend();

        // Set legend entries.
        PieChartData data = getData();
        if (legend != null && getData().getDataTable() != null)
        {
            String catAxisProp = data.getCategoriesProperty();
            String[] values = getData().getDataTable().getColumn(catAxisProp).getStringValues();
            if (values != null && values.length > 0)
            {
                for (int x = 0; x < values.length; x++)
                {
                    String value = values[x];
                    legend.add(new ChartLegend.ChartLegendEntry(value, getColor(x)));
                }
            }
            legend.build();
        }
    }

    private void buildTooltip()
    {
        tooltip = new PieChartTooltip();

        addOnAreaChartCentered(tooltip);
    }

    protected void addOnAreaChartCentered(Group group)
    {
        chartArea.add(group);
        group.setX(getChartWidth() / 2);
        group.setY(getChartHeight() / 2);
        group.setAlpha(1d);
    }

    // TODO: Use color strategy.
    protected IColor getColor(int position)
    {
        int defaultColorsSize = DEFAULT_SLICE_COLORS.length;

        if (position < defaultColorsSize)
        {
            return DEFAULT_SLICE_COLORS[position];
        }
        return new Color(position * 20, 128, 0);
    }

    public final PieChart setData(PieChartData data)
    {
        if (null != data)
        {
            getAttributes().put(ChartAttribute.PIE_CHART_DATA.getProperty(), data.getJSO());
        }
        PieChart.this.fireEvent(new DataReloadedEvent(this));

        return this;
    }

    public final PieChartData getData()
    {
        if (getAttributes().isDefined(ChartAttribute.PIE_CHART_DATA))
        {
            PieChartData.PieChartDataJSO jso = getAttributes().getObject(ChartAttribute.PIE_CHART_DATA.getProperty()).cast();

            return new PieChartData(jso);
        }
        return null;
    }

    

    public final double getRadius(double chartWidth, double chartHeight)
    {
        double forSize = chartHeight;

        if (getChartWidth() < forSize) forSize = chartWidth;

        return forSize / 2;
    }

    private final native String getLabel(double perc)
    /*-{
		var numb = perc;

		return numb.toFixed(2) + "%";
    }-*/;

    public static class PieChartFactory extends ChartFactory<PieChart>
    {
        public PieChartFactory()
        {
            super(ChartNodeType.PIE_CHART);

            addAttribute(ChartAttribute.RADIUS, true);

            addAttribute(ChartAttribute.PIE_CHART_DATA, true);
        }

        @Override
        public boolean addNodeForContainer(IContainer<?, ?> container, Node<?> node, ValidationContext ctx)
        {
            return false;
        }

        @Override
        protected PieChart container(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new PieChart(node, ctx);
        }
    }

    public static class PieSlice extends Slice
    {
        private boolean m_animating = false;

        public PieSlice(double radius, double sofar, double value)
        {
            super(radius, Math.PI * (-0.5 + 2 * sofar), Math.PI * (-0.5 + 2 * (sofar + value)), false);
        }

        public static double buildStartAngle(double sofar)
        {
            return Math.PI * (-0.5 + 2 * sofar);
        }

        public static double buildEngAngle(double sofar, double value)
        {
            return Math.PI * (-0.5 + 2 * (sofar + value));
        }

        public final void setAnimating(boolean animating)
        {
            m_animating = animating;
        }

        public final boolean isAnimating()
        {
            return m_animating;
        }
    }
}