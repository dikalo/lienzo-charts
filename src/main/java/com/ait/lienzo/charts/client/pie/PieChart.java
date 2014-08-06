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

package com.ait.lienzo.charts.client.pie;

import com.ait.lienzo.charts.client.pie.PieChartData.PieChartEntry;
import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Slice;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class PieChart extends Group
{
    private double       m_radius;

    private PieChartData m_data;

    public PieChart(double radius, PieChartData data)
    {
        m_data = data;

        m_radius = radius;

        if (radius < 1)
        {
            return;
        }
        if ((null == data) || (data.size() < 1))
        {
            return;
        }
        getMetaData().put("creator", "Dean S. Jones").put("version", "1.0.1.SNAPSHOT");

        final PieChartEntry[] values = data.getEntries();

        double sofar = 0;

        double total = 0;

        for (int i = 0; i < values.length; i++)
        {
            total += values[i].getValue();
        }
        Group slices = new Group();

        Group labels = new Group();

        labels.setListening(false);

        for (int i = 0; i < values.length; i++)
        {
            double value = values[i].getValue() / total;

            final PieSlice slice = new PieSlice(radius, sofar, value);

            slice.setFillColor(values[i].getColor()).setStrokeColor(ColorName.BLACK).setStrokeWidth(3);

            slice.addNodeMouseEnterHandler(new NodeMouseEnterHandler()
            {
                @Override
                public void onNodeMouseEnter(NodeMouseEnterEvent event)
                {
                    if (false == slice.isAnimating())
                    {
                        slice.setAnimating(true);

                        slice.setListening(false);

                        slice.getLayer().batch();

                        slice.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.SCALE(1.3, 1.3)), 333, new AnimationCallback()
                        {
                            @Override
                            public void onClose(IAnimation animation, IAnimationHandle handle)
                            {
                                slice.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.SCALE(1, 1)), 333, new AnimationCallback()
                                {
                                    @Override
                                    public void onClose(IAnimation animation, IAnimationHandle handle)
                                    {
                                        slice.setScale(null);

                                        slice.setListening(true);

                                        slice.setAnimating(false);

                                        slice.getLayer().batch();
                                    }
                                });
                            }
                        });
                    }
                }
            });
            slices.add(slice);

            double s_ang = Math.PI * (2.0 * sofar);

            double e_ang = Math.PI * (2.0 * (sofar + value));

            double n_ang = (s_ang + e_ang) / 2.0;

            if (n_ang > (Math.PI * 2.0))
            {
                n_ang = n_ang - (Math.PI * 2.0);
            }
            else if (n_ang < 0)
            {
                n_ang = n_ang + (Math.PI * 2.0);
            }
            double lx = Math.sin(n_ang) * (radius + 50);

            double ly = 0 - Math.cos(n_ang) * (radius + 50);

            TextAlign align;

            if (n_ang <= (Math.PI * 0.5))
            {
                lx += 2;

                align = TextAlign.LEFT;
            }
            else if ((n_ang > (Math.PI * 0.5)) && (n_ang <= Math.PI))
            {
                lx += 2;

                align = TextAlign.LEFT;
            }
            else if ((n_ang > Math.PI) && (n_ang <= (Math.PI * 1.5)))
            {
                lx -= 2;

                align = TextAlign.RIGHT;
            }
            else
            {
                lx -= 2;

                align = TextAlign.RIGHT;
            }
            String label = values[i].getLabel();

            if (null == label)
            {
                label = "";
            }
            else
            {
                label = label + " ";
            }
            Text text = new Text(label + getLabel(value * 100), "Calibri", "bold", 14).setFillColor(ColorName.BLACK).setX(lx).setY(ly).setTextAlign(align).setTextBaseLine(TextBaseLine.MIDDLE);

            Line line = new Line((Math.sin(n_ang) * radius), 0 - (Math.cos(n_ang) * radius), (Math.sin(n_ang) * (radius + 50)), 0 - (Math.cos(n_ang) * (radius + 50))).setStrokeColor(ColorName.BLACK).setStrokeWidth(3);

            labels.add(text);

            labels.add(line);

            sofar += value;
        }
        add(labels);

        add(slices);
    }

    private final native String getLabel(double perc)
    /*-{
		var numb = perc;

		return numb.toFixed(2) + "%";
    }-*/;

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject object = new JSONObject();

        object.put("type", new JSONString("Lienzo.PieChart"));

        if (false == getMetaData().isEmpty())
        {
            object.put("meta", new JSONObject(getMetaData().getJSO()));
        }
        JSONObject attributes = new JSONObject(getAttributes().getJSO());

        attributes.put("radius", new JSONNumber(m_radius));

        object.put("attributes", attributes);

        JSONArray data = new JSONArray();

        PieChartEntry[] entries = m_data.getEntries();

        for (int i = 0; i < entries.length; i++)
        {
            JSONObject entry = new JSONObject();

            entry.put("value", new JSONNumber(entries[i].getValue()));

            entry.put("label", new JSONString(entries[i].getLabel()));

            entry.put("color", new JSONString(entries[i].getColor()));

            data.set(i, entry);
        }
        object.put("data", data);

        return object;
    }

    private static class PieSlice extends Slice
    {
        private boolean m_animating = false;

        public PieSlice(double radius, double sofar, double value)
        {
            super(radius, Math.PI * (-0.5 + 2 * sofar), Math.PI * (-0.5 + 2 * (sofar + value)), false);
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