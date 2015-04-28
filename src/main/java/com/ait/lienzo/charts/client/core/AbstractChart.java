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

package com.ait.lienzo.charts.client.core;

import static com.ait.lienzo.client.core.AttributeOp.any;
import static com.ait.lienzo.client.core.animation.AnimationProperties.toPropertyList;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.ALPHA;
import static com.ait.lienzo.client.core.animation.AnimationTweener.LINEAR;

import com.ait.lienzo.charts.client.core.legend.ChartLegend;
import com.ait.lienzo.charts.client.core.resizer.ChartResizeAreaEvent;
import com.ait.lienzo.charts.client.core.resizer.ChartResizeAreaEventHandler;
import com.ait.lienzo.charts.client.core.resizer.ChartResizeEvent;
import com.ait.lienzo.charts.client.core.resizer.ChartResizeEventHandler;
import com.ait.lienzo.charts.client.core.resizer.ChartResizer;
import com.ait.lienzo.charts.shared.core.types.ChartAlign;
import com.ait.lienzo.charts.shared.core.types.ChartDirection;
import com.ait.lienzo.charts.shared.core.types.ChartOrientation;
import com.ait.lienzo.charts.shared.core.types.LegendAlign;
import com.ait.lienzo.charts.shared.core.types.LegendPosition;
import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.event.AnimationFrameAttributesChangedBatcher;
import com.ait.lienzo.client.core.event.AttributesChangedEvent;
import com.ait.lienzo.client.core.event.AttributesChangedHandler;
import com.ait.lienzo.client.core.event.IAttributesChangedBatcher;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.GroupOf;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.shape.storage.IStorageEngine;
import com.ait.lienzo.client.core.shape.storage.PrimitiveFastArrayStorageEngine;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.tooling.common.api.flow.Flows.BooleanOp;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 *  <p>Base chart implementation class.</p>
 *  <p>It provides:</p>
 *  <ul>
 *      <li>Five areas (group nodes):</li>
 *      <ul>
 *          <li>Chart area: Group node that contains all the chart shapes that represent the values.</li>          
 *          <li>Bottom area: Group node can usually contains axis labels or the chart legend.</li>
 *          <li>Top area: Group node usually contains the chart title or the chart legend.</li>
 *          <li>Left area: Group node usually contains axis labels or the chart legend.</li>
 *          <li>Right area: Group node usually contains axis labels or the chart legend.</li>
 *      </ul>      
 *      <li>Chart title.</li>
 *      <li>Chart legend.</li>
 *      <li>Chart resizer.</li>
 *  </ul>
 *  <p>It listens the <code>AttributesChangedEvent</code> for attributes <code>X</code>, <code>Y</code>, <code>WIDTH</code> and <code>HEIGHT</code>.</p>  
 *  
 */
public abstract class AbstractChart<T extends AbstractChart<T>> extends GroupOf<IPrimitive<?>, T>
{
    public static final double          DEFAULT_MARGIN             = 50;

    private static final BooleanOp      XYWH_OP                    = any(Attribute.X, Attribute.Y, Attribute.WIDTH, Attribute.HEIGHT);

    protected IAttributesChangedBatcher attributesChangedBatcher   = new AnimationFrameAttributesChangedBatcher();

    protected final Group               chartArea                  = new Group();

    protected final Group               topArea                    = new Group();

    protected final Group               bottomArea                 = new Group();

    protected final Group               rightArea                  = new Group();

    protected final Group               leftArea                   = new Group();

    protected ChartResizer              resizer                    = null;

    protected Text                      chartTitle                 = null;

    protected ChartLegend               legend                     = null;

    private double                      m_defaultAnimationDuration = 500;

    protected AbstractChart(ChartNodeType type)
    {
        super(type, new PrimitiveFastArrayStorageEngine());
    }

    protected AbstractChart(ChartNodeType type, IStorageEngine<IPrimitive<?>> storage)
    {
        super(type, storage);
    }

    public AbstractChart(ChartNodeType type, JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(type, node, ctx);
    }

    @Override
    public JSONObject toJSONObject()
    {
        final JSONObject object = new JSONObject();

        object.put("type", new JSONString(getGroupType().getValue()));

        if (false == getMetaData().isEmpty())
        {
            object.put("meta", new JSONObject(getMetaData().getJSO()));
        }
        object.put("attributes", new JSONObject(getAttributes().getJSO()));

        object.put("storage", getStorageEngine().toJSONObject());

        return object;
    }

    @Override
    public IStorageEngine<IPrimitive<?>> getDefaultStorageEngine()
    {
        return new PrimitiveFastArrayStorageEngine();
    }

    public final T setDefaultAnimationDuration(final double duration)
    {
        m_defaultAnimationDuration = Math.max(duration, 1);

        return cast();
    }

    public final double getDefaultAnimationDuration()
    {
        return m_defaultAnimationDuration;
    }

    public abstract static class ChartFactory<T extends AbstractChart<T>> extends GroupOfFactory<IPrimitive<?>, T>
    {
        public ChartFactory(ChartNodeType type)
        {
            super(type);
            addAttribute(ChartAttribute.X, true);
            addAttribute(ChartAttribute.Y, true);
            addAttribute(ChartAttribute.WIDTH, true);
            addAttribute(ChartAttribute.HEIGHT, true);
            addAttribute(ChartAttribute.NAME, true);
            addAttribute(ChartAttribute.MARGIN_LEFT, false);
            addAttribute(ChartAttribute.MARGIN_RIGHT, false);
            addAttribute(ChartAttribute.MARGIN_BOTTOM, false);
            addAttribute(ChartAttribute.MARGIN_TOP, false);
            addAttribute(ChartAttribute.ALIGN, false);
            addAttribute(ChartAttribute.DIRECTION, false);
            addAttribute(ChartAttribute.ORIENTATION, false);
            addAttribute(ChartAttribute.SHOW_TITLE, false);
            addAttribute(ChartAttribute.FONT_FAMILY, false);
            addAttribute(ChartAttribute.FONT_STYLE, false);
            addAttribute(ChartAttribute.FONT_SIZE, false);
            addAttribute(ChartAttribute.LEGEND_POSITION, false);
            addAttribute(ChartAttribute.LEGEND_ALIGN, false);
            addAttribute(ChartAttribute.RESIZABLE, false);
        }

        @Override
        public boolean addNodeForContainer(IContainer<?, ?> container, Node<?> node, ValidationContext ctx)
        {
            return false;
        }
    }

    public abstract T init();

    public abstract T init(double duration);

    public abstract T init(AnimationTweener tweener, double duration);

    public T draw()
    {
        // Add the area node containers.
        add(chartArea); // Area for drawing the chart.
        add(topArea); // Area for top padding.
        add(bottomArea); // Area for bottom padding.
        add(leftArea); // Area for left padding.
        add(rightArea); // Area for right padding.

        // Position the areas.
        moveAreas(0d, 0d);

        // Chart title.
        buildTitle();

        // Chart Legend.
        buildLegend();

        // Call parent draw implementation.
        doDraw();

        // Add the resizer.
        buildResizer();

        setAttributesChangedBatcher(attributesChangedBatcher);

        AttributesChangedHandler handler = new AttributesChangedHandler()
        {
            @Override
            public void onAttributesChanged(AttributesChangedEvent event)
            {
                if (event.evaluate(XYWH_OP))
                {
                    moveAreas(getX(), getY());
                }
            }
        };
        // Attribute change handlers.
        this.addAttributesChangedHandler(Attribute.X, handler);
        this.addAttributesChangedHandler(Attribute.Y, handler);
        this.addAttributesChangedHandler(Attribute.WIDTH, handler);
        this.addAttributesChangedHandler(Attribute.HEIGHT, handler);

        return cast();
    }

    protected void moveAreas(final double x, final double y)
    {
        Double[] leftAreaPos = getLeftAreaPosition(x, y);
        Double[] rightAreaPos = getRightAreaPosition(x, y);
        Double[] topAreaPos = getTopAreaPosition(x, y);
        Double[] bottomAreaPos = getBottomAreaPosition(x, y);
        Double[] chartAreaPos = getChartAreaPosition(x, y);
        leftArea.setX(leftAreaPos[0]).setY(leftAreaPos[1]);
        rightArea.setX(rightAreaPos[0]).setY(rightAreaPos[1]);
        topArea.setX(topAreaPos[0]).setY(topAreaPos[1]);
        bottomArea.setX(bottomAreaPos[0]).setY(bottomAreaPos[1]);
        chartArea.setX(chartAreaPos[0]).setY(chartAreaPos[1]);
        chartArea.moveToTop();
        if (getChartResizer() != null) getChartResizer().moveToTop();
    }

    protected void applyAlphaToAreas(final double alpha)
    {
        final double duration = Math.min(getDefaultAnimationDuration() / 2, 200);

        leftArea.animate(LINEAR, toPropertyList(ALPHA(alpha)), duration);

        rightArea.animate(LINEAR, toPropertyList(ALPHA(alpha)), duration);

        topArea.animate(LINEAR, toPropertyList(ALPHA(alpha)), duration);

        bottomArea.animate(LINEAR, toPropertyList(ALPHA(alpha)), duration);

        chartArea.animate(LINEAR, toPropertyList(ALPHA(alpha)), duration);
    }

    protected void buildLegend()
    {
        LegendPosition legendPosition = getLegendPosition();
        //LegendAlign legendAlign = getLegendAlignment();
        if (!LegendPosition.NONE.equals(legendPosition))
        {
            legend = new ChartLegend();
            Double[] legendPositions = getLegendPositions(getChartWidth(), getChartHeight());
            double xLegend = legendPositions[0];
            double yLegend = legendPositions[1];
            switch (legendPosition)
            {
                case TOP:
                    topArea.add(legend.setX(xLegend).setY(yLegend));
                    break;
                case LEFT:
                    leftArea.add(legend.setX(xLegend).setY(yLegend));
                    break;
                case RIGHT:
                    rightArea.add(legend.setX(xLegend).setY(yLegend));
                    break;
                case INSIDE:
                    chartArea.add(legend.setX(xLegend).setY(yLegend));
                    break;
                default:
                    bottomArea.add(legend.setX(xLegend).setY(yLegend));
                    break;
            }
            // Set horizontal orientation.
            legend.setOrientation(getLegendOrientation());

            legend.moveToTop();
        }
    }

    public Double[] getLegendPositions(final double w, final double h)
    {
        LegendPosition legendPosition = getLegendPosition();
        //LegendAlign legendAlign = getLegendAlignment();
        if (!LegendPosition.NONE.equals(legendPosition))
        {
            legend = new ChartLegend();
            double xLegend = 0;
            double yLegend = 0;
            // TODO: legendAlign
            switch (legendPosition)
            {
                case TOP:
                    xLegend = w / 2;
                    yLegend = 5;
                    break;
                case LEFT:
                    xLegend = 5;
                    yLegend = h / 2;
                    break;
                case RIGHT:
                    xLegend = 5;
                    yLegend = h / 2;
                    break;
                case INSIDE:
                    xLegend = w / 2;
                    yLegend = 2;
                    break;
                default:
                    xLegend = w / 2;
                    yLegend = 5;
                    break;
            }
            return new Double[] { xLegend, yLegend };
        }
        return null;
    }

    protected void buildTitle()
    {
        if (isShowTitle())
        {
            chartTitle = new Text(getName(), getFontFamily(), getFontStyle(), getFontSize()).setFillColor(ColorName.BLACK).setTextAlign(TextAlign.CENTER).setTextBaseLine(TextBaseLine.MIDDLE);
        }
    }

    protected void buildResizer()
    {
        if (isResizable())
        {
            resizer = new ChartResizer(getWidth(), getHeight());

            resizer.setX(getX()).setY(getY()).moveToTop();

            resizer.addChartResizeEventHandler(new ChartResizeEventHandler()
            {
                @Override
                public void onChartResize(ChartResizeEvent event)
                {
                    AbstractChart.this.onChartResize(event);
                }
            });
            resizer.addChartResizeAreaEventHandler(new ChartResizeAreaEventHandler()
            {
                @Override
                public void onChartResizeArea(ChartResizeAreaEvent event)
                {
                    if (event.isEnteringResizeArea())
                    {
                        applyAlphaToAreas(0.2);
                    }
                    else
                    {
                        applyAlphaToAreas(1);
                    }
                }
            });
            add(resizer);
        }
    }

    protected void onChartResize(ChartResizeEvent event)
    {
        // Fire event for chart resize.
        fireEvent(event);
    }

    public HandlerRegistration addChartResizeEventHandler(ChartResizeEventHandler handler)
    {
        return addEnsureHandler(ChartResizeEvent.TYPE, handler);
    }

    protected ChartLegend.ChartLegendOrientation getLegendOrientation()
    {
        switch (getLegendPosition())
        {
            case LEFT:
            case RIGHT:
                return ChartLegend.ChartLegendOrientation.VERTICAL;
            default:
                return ChartLegend.ChartLegendOrientation.HORIZONTAL;
        }
    }

    protected Double[] getLeftAreaPosition(Double x, Double y)
    {
        double marginTop = getMarginTop();
        Double _x = x != null ? x : null;
        Double _y = y != null ? y + marginTop : null;
        return new Double[] { _x, _y };
    }

    protected Double[] getRightAreaPosition(Double x, Double y)
    {
        double marginLeft = getMarginLeft();
        double marginTop = getMarginTop();
        Double _x = x != null ? x + getChartWidth() + marginLeft : null;
        Double _y = y != null ? y + marginTop : null;
        return new Double[] { _x, _y };
    }

    protected Double[] getTopAreaPosition(Double x, Double y)
    {
        double marginLeft = getMarginLeft();
        Double _x = x != null ? x + marginLeft : null;
        Double _y = y != null ? y : null;
        return new Double[] { _x, _y };
    }

    protected Double[] getBottomAreaPosition(Double x, Double y)
    {
        double marginLeft = getMarginLeft();
        double marginTop = getMarginTop();
        Double _x = x != null ? x + marginLeft : null;
        Double _y = y != null ? y + marginTop + getChartHeight() : null;
        return new Double[] { _x, _y };
    }

    protected Double[] getChartAreaPosition(Double x, Double y)
    {
        double marginLeft = getMarginLeft();
        double marginTop = getMarginTop();
        Double _x = x != null ? x + marginLeft : null;
        Double _y = y != null ? y + marginTop : null;
        return new Double[] { _x, _y };
    }

    protected void clear()
    {
        if (chartTitle != null) chartTitle.removeFromParent();
        if (legend != null) legend.removeFromParent();
        if (resizer != null) resizer.removeFromParent();

        // Ensure that all shapes are removed. 
        this.removeAll();
    }

    protected abstract void doDraw();

    public Text getChartTitle()
    {
        return chartTitle;
    }

    public ChartLegend getChartLegend()
    {
        return legend;
    }

    public ChartResizer getChartResizer()
    {
        return resizer;
    }

    protected double getChartHeight(double originalHeight)
    {
        return originalHeight - (getMarginTop() + getMarginBottom());
    }

    protected double getChartWidth(double originalWidth)
    {
        return originalWidth - (getMarginLeft() + getMarginRight());
    }

    public double getChartHeight()
    {
        return getAttributes().getHeight();
    }

    public double getChartWidth()
    {
        return getAttributes().getWidth();
    }

    public double getHeight()
    {
        return getAttributes().getHeight() + getMarginTop() + getMarginBottom();
    }

    public double getWidth()
    {
        return getAttributes().getWidth() + getMarginLeft() + getMarginRight();
    }

    public String getFontFamily()
    {
        return getAttributes().getFontFamily();
    }

    public String getFontStyle()
    {
        return getAttributes().getFontStyle();
    }

    public double getFontSize()
    {
        return getAttributes().getFontSize();
    }

    public T setWidth(double width)
    {
        getAttributes().setWidth(width);

        return cast();
    }

    public T setHeight(double height)
    {
        getAttributes().setHeight(height);

        return cast();
    }

    public T setFontFamily(String f)
    {
        getAttributes().setFontFamily(f);

        return cast();
    }

    public T setFontSize(int size)
    {
        getAttributes().setFontSize(size);

        return cast();
    }

    public T setFontStyle(String f)
    {
        getAttributes().setFontStyle(f);

        return cast();
    }

    public T setAlignment(ChartAlign chartAlignment)
    {
        if (null != chartAlignment)
        {
            getAttributes().put(ChartAttribute.ALIGN.getProperty(), chartAlignment.getValue());
        }
        else
        {
            getAttributes().remove(ChartAttribute.ALIGN.getProperty());
        }
        return cast();
    }

    public final ChartAlign getAlignment()
    {
        return ChartAlign.lookup(getAttributes().getString(ChartAttribute.ALIGN.getProperty()));
    }

    public T setOrientation(ChartOrientation chartOrientation)
    {
        if (null != chartOrientation)
        {
            getAttributes().put(ChartAttribute.ORIENTATION.getProperty(), chartOrientation.getValue());
        }
        else
        {
            getAttributes().remove(ChartAttribute.ORIENTATION.getProperty());
        }
        return cast();
    }

    public final ChartOrientation getOrientation()
    {
        return ChartOrientation.lookup(getAttributes().getString(ChartAttribute.ORIENTATION.getProperty()));
    }

    public T setDirection(ChartDirection chartDirection)
    {
        if (null != chartDirection)
        {
            getAttributes().put(ChartAttribute.DIRECTION.getProperty(), chartDirection.getValue());
        }
        else
        {
            getAttributes().remove(ChartAttribute.DIRECTION.getProperty());
        }
        return cast();
    }

    public final ChartDirection getDirection()
    {
        return ChartDirection.lookup(getAttributes().getString(ChartAttribute.DIRECTION.getProperty()));
    }

    public T setLegendPosition(LegendPosition legendPosition)
    {
        if (null != legendPosition)
        {
            getAttributes().put(ChartAttribute.LEGEND_POSITION.getProperty(), legendPosition.getValue());
        }
        else
        {
            getAttributes().remove(ChartAttribute.LEGEND_POSITION.getProperty());
        }
        return cast();
    }

    public final LegendPosition getLegendPosition()
    {
        return LegendPosition.lookup(getAttributes().getString(ChartAttribute.LEGEND_POSITION.getProperty()));
    }

    public T setLegendAlignment(LegendAlign legendAlign)
    {
        if (null != legendAlign)
        {
            getAttributes().put(ChartAttribute.LEGEND_ALIGN.getProperty(), legendAlign.getValue());
        }
        else
        {
            getAttributes().remove(ChartAttribute.LEGEND_ALIGN.getProperty());
        }
        return cast();
    }

    public final LegendAlign getLegendAlignment()
    {
        return LegendAlign.lookup(getAttributes().getString(ChartAttribute.LEGEND_ALIGN.getProperty()));
    }

    public final T setShowTitle(boolean showTitle)
    {
        getAttributes().put(ChartAttribute.SHOW_TITLE.getProperty(), showTitle);

        return cast();
    }

    public final boolean isShowTitle()
    {
        if (getAttributes().isDefined(ChartAttribute.SHOW_TITLE))
        {
            return getAttributes().getBoolean(ChartAttribute.SHOW_TITLE.getProperty());
        }
        return true;
    }

    public final T setResizable(boolean resizable)
    {
        getAttributes().put(ChartAttribute.RESIZABLE.getProperty(), resizable);
        return cast();
    }

    public final boolean isResizable()
    {
        if (getAttributes().isDefined(ChartAttribute.RESIZABLE))
        {
            return getAttributes().getBoolean(ChartAttribute.RESIZABLE.getProperty());
        }
        return true;
    }

    public final double getMarginLeft()
    {
        if (getAttributes().isDefined(ChartAttribute.MARGIN_LEFT))
        {
            return getAttributes().getDouble(ChartAttribute.MARGIN_LEFT.getProperty());
        }
        return DEFAULT_MARGIN;
    }

    public final T setMarginLeft(final double margin)
    {
        getAttributes().put(ChartAttribute.MARGIN_LEFT.getProperty(), margin);

        return cast();
    }

    public final double getMarginTop()
    {
        if (getAttributes().isDefined(ChartAttribute.MARGIN_TOP))
        {
            return getAttributes().getDouble(ChartAttribute.MARGIN_TOP.getProperty());
        }
        return DEFAULT_MARGIN;
    }

    public final T setMarginTop(final double margin)
    {
        getAttributes().put(ChartAttribute.MARGIN_TOP.getProperty(), margin);

        return cast();
    }

    public final double getMarginRight()
    {
        if (getAttributes().isDefined(ChartAttribute.MARGIN_RIGHT))
        {
            return getAttributes().getDouble(ChartAttribute.MARGIN_RIGHT.getProperty());
        }
        return DEFAULT_MARGIN;
    }

    public final T setMarginRight(final double margin)
    {
        getAttributes().put(ChartAttribute.MARGIN_RIGHT.getProperty(), margin);

        return cast();
    }

    public final double getMarginBottom()
    {
        if (getAttributes().isDefined(ChartAttribute.MARGIN_BOTTOM))
        {
            return getAttributes().getDouble(ChartAttribute.MARGIN_BOTTOM.getProperty());
        }
        return DEFAULT_MARGIN;
    }

    public final T setMarginBottom(final double margin)
    {
        getAttributes().put(ChartAttribute.MARGIN_BOTTOM.getProperty(), margin);

        return cast();
    }
}
