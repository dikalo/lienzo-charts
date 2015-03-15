
package com.ait.lienzo.charts.client.core.xy.bar;

import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;

public class BarChartLabel extends Group
{
    private static final double ANIMATION_DURATION = 500;

    private static final String FONT_FAMILY        = "Verdana";

    private static final String FONT_STYLE         = "";

    private static final int    FONT_SIZE          = 6;

    private static final IColor LABEL_COLOR        = ColorName.BLACK;

    private AxisLabel           axisLabel;

    private Text                label;

    private Rectangle           labelContainer;

    public BarChartLabel(AxisLabel axisLabel)
    {
        this.axisLabel = axisLabel;
        build();
    }

    private void build()
    {
        label = new Text(axisLabel.getText(), FONT_FAMILY, FONT_STYLE, FONT_SIZE).setFillColor(LABEL_COLOR).setTextAlign(TextAlign.LEFT).setTextBaseLine(TextBaseLine.TOP);
        label.setID("label" + axisLabel.getIndex());
        labelContainer = new Rectangle(1, 1);
        add(label);
        add(labelContainer);
        labelContainer.setAlpha(0.01);
        labelContainer.moveToTop();
    }

    public void setAttributes(final AxisLabel axisLabel, final Double x, final Double y, final Double width, final Double height, final boolean animate)
    {
        this.axisLabel = axisLabel;
        String text = axisLabel.getText();
        label.setText(text);
        if (x != null) this.setX(x);
        if (y != null) this.setY(y);
        setShapeAttributes(label, null, null, width, height, animate);
        setShapeAttributes(labelContainer, null, null, width, height, animate);
    }

    public void clear()
    {
        label.removeFromParent();
        labelContainer.removeFromParent();
    }

    public Text getLabel()
    {
        return label;
    }

    public Rectangle getLabelContainer()
    {
        return labelContainer;
    }

    public AxisLabel getAxisLabel()
    {
        return axisLabel;
    }

    public double getLabelWidth()
    {
        return label.getBoundingBox().getWidth();
    }

    public double getLabelHeight()
    {
        return label.getBoundingBox().getHeight();
    }

    public String getId()
    {
        return label.getID();

    }

    private void setShapeAttributes(Shape<?> shape, Double x, Double y, Double width, Double height, boolean animate)
    {
        if (animate)
        {
            AnimationProperties animationProperties = new AnimationProperties();
            if (width != null) animationProperties.push(AnimationProperty.Properties.WIDTH(width));
            if (height != null) animationProperties.push(AnimationProperty.Properties.HEIGHT(height));
            if (x != null) animationProperties.push(AnimationProperty.Properties.X(x));
            if (y != null) animationProperties.push(AnimationProperty.Properties.Y(y));
            shape.animate(AnimationTweener.LINEAR, animationProperties, ANIMATION_DURATION);

        }
        else
        {
            if (shape != null)
            {
                if (x != null) shape.setX(x);
                if (y != null) shape.setY(y);
                if (width != null) shape.getAttributes().setWidth(width);
                if (height != null) shape.getAttributes().setHeight(height);
            }
        }
    }
}
