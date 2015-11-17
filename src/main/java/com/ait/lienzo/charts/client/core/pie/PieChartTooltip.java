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
   
   Author: Roger Martinez - Red Hat
 */

package com.ait.lienzo.charts.client.core.pie;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.Triangle;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;

public class PieChartTooltip extends Group
{
    public static final double  TRIANGLE_SIZE          = 10;

    private static final double TOOLTIP_PADDING_WIDTH  = 25;

    private static final double TOOLTIP_PADDING_HEIGHT = 25;

    private static final IColor TOOLTIP_COLOR          = ColorName.WHITESMOKE;

    private static final String FONT_FAMILY            = "Verdana";

    private static final String CATEGORIES_FONT_STYLE  = "";

    private static final String VALUES_FONT_STYLE      = "bold";

    private static final int    FONT_SIZE              = 10;

    private static final IColor LABEL_COLOR            = ColorName.BLACK;

    private Rectangle           rectangle;

    private Triangle            triangle;

    private Triangle            tmasking;

    private Text                categoriesText;

    private Text                valuesText;

    private String              _categoriesText;

    private String              _valuesText;

    private static final Shadow SHADOW                 = new Shadow(ColorName.BLACK.getColor().setA(0.80), 10, 3, 3);

    public PieChartTooltip()
    {
        build();
    }

    protected PieChartTooltip build()
    {
        rectangle = new Rectangle(1, 1).setFillColor(TOOLTIP_COLOR).setCornerRadius(5).setStrokeWidth(1).setShadow(SHADOW);
        triangle = new Triangle(new Point2D(1, 1), new Point2D(1, 1), new Point2D(1, 1)).setFillColor(TOOLTIP_COLOR).setStrokeWidth(1).setShadow(SHADOW);
        tmasking = new Triangle(new Point2D(1, 1), new Point2D(1, 1), new Point2D(1, 1)).setFillColor(TOOLTIP_COLOR);
        categoriesText = new Text("", FONT_FAMILY, CATEGORIES_FONT_STYLE, FONT_SIZE).setFillColor(LABEL_COLOR).setTextAlign(TextAlign.LEFT).setTextBaseLine(TextBaseLine.MIDDLE);
        valuesText = new Text("", FONT_FAMILY, VALUES_FONT_STYLE, FONT_SIZE).setFillColor(LABEL_COLOR).setTextAlign(TextAlign.LEFT).setTextBaseLine(TextBaseLine.MIDDLE);
        add(rectangle);
        add(triangle);
        add(tmasking);
        add(categoriesText);
        add(valuesText);
        categoriesText.moveToTop();
        valuesText.moveToTop();
        setVisible(false);
        setListening(false);
        return this;
    }

    public PieChartTooltip show(final double x, final double y)
    {
        this.categoriesText.setText(_categoriesText);
        BoundingBox bb = categoriesText.getBoundingBox();
        final double ctw = bb.getWidth();
        final double cth = bb.getHeight();
        this.valuesText.setText(_valuesText);
        bb = valuesText.getBoundingBox();
        final double vtw = bb.getWidth();
        final double vth = bb.getHeight();
        final double rw = (ctw > vtw ? ctw : vtw) + TOOLTIP_PADDING_WIDTH;
        final double rh = (cth + vth) + TOOLTIP_PADDING_HEIGHT;
        rectangle.setWidth(rw).setHeight(rh).setCornerRadius(5);
        final double rx = rectangle.getX();
        final double ry = rectangle.getY();
        triangle.setPoints(new Point2D(rx + rw / 2 - TRIANGLE_SIZE, ry + rh), new Point2D(rx + rw / 2, rh + TRIANGLE_SIZE), new Point2D(rx + rw / 2 + TRIANGLE_SIZE, ry + rh));
        tmasking.setPoints(new Point2D(rx + rw / 2 - TRIANGLE_SIZE - 3, ry + rh - 3), new Point2D(rx + rw / 2, rh + TRIANGLE_SIZE - 3), new Point2D(rx + rw / 2 + TRIANGLE_SIZE + 3, ry + rh - 3));
        final double vtx = rw / 2 - vtw / 2;
        final double ctx = rw / 2 - ctw / 2;
        final double vty = rh / 2 - vth / 2;
        final double cty = vty + cth + 1;
        this.categoriesText.setX(ctx).setY(cty);
        this.valuesText.setX(vtx).setY(vty);
        setX(x - rw / 2);
        setY(y - rh);
        moveToTop();
        setVisible(true);
        getLayer().batch();
        return this;
    }

    public PieChartTooltip hide()
    {
        setVisible(false);

        getLayer().batch();

        return this;
    }

    public PieChartTooltip setValues(String categoriesText, String valuesText)
    {
        this._categoriesText = categoriesText;

        this._valuesText = valuesText;

        return this;
    }
}
