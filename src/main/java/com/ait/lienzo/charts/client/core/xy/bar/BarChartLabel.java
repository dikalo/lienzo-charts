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

import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;

public class BarChartLabel extends Group
{
    private static final String FONT_FAMILY = "Verdana";

    private static final String FONT_STYLE  = "";

    private static final int    FONT_SIZE   = 6;

    private static final IColor LABEL_COLOR = ColorName.BLACK;

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

    public void set(final AxisLabel axisLabel)
    {
        this.axisLabel = axisLabel;
        String text = axisLabel.getText();
        label.setText(text);
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

    /* **************************************************************************** */
    /* Package protected access modifier for methods that expose child group nodes. */
    /* **************************************************************************** */

    Text getLabel()
    {
        return label;
    }

    Rectangle getLabelContainer()
    {
        return labelContainer;
    }
}
