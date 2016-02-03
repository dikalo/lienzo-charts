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

package com.ait.lienzo.charts.client.core.animation;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;

// TODO: Really necessary?
public class PointsAnimationProperty implements AnimationProperty
{
    private double[]        m_orig_0;

    private double[]        m_orig_1;

    private double[]        m_targ_0;

    private double[]        m_targ_1;

    private final Attribute m_attribute;

    public PointsAnimationProperty(final Point2D target0, final Point2D target1, final Attribute attribute)
    {
        m_targ_0 = new double[] { target0.getX(), target0.getY() };

        m_targ_1 = new double[] { target1.getX(), target1.getY() };

        m_attribute = attribute;
    }

    @Override
    public boolean init(final Node<?> node)
    {
        if ((node != null) && (m_attribute != null) && (m_attribute.isAnimatable()) && (node.getAttributeSheet().contains(m_attribute)))
        {
            final Point2DArray array = node.getAttributes().getPoints();

            if (null == array)
            {
                m_orig_0 = new double[] { 1, 1 };
                m_orig_1 = new double[] { 1, 1 };
            }
            else
            {
                m_orig_0 = new double[] { array.get(0).getX(), array.get(0).getY() };
                m_orig_1 = new double[] { array.get(1).getX(), array.get(1).getY() };
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean apply(final Node<?> node, final double percent)
    {
        node.getAttributes().setPoints(new Point2DArray(new Point2D(caculate(m_orig_0[0], m_targ_0[0], percent), caculate(m_orig_0[1], m_targ_0[1], percent)), new Point2D(caculate(m_orig_1[0], m_targ_1[0], percent), caculate(m_orig_1[1], m_targ_1[1], percent))));

        return true;
    }

    private double caculate(final double origin, final double target, final double percent)
    {
        return origin + ((target - origin) * percent);
    }

    @Override
    public PointsAnimationProperty copy()
    {
        return new PointsAnimationProperty(new Point2D(m_targ_0[0], m_targ_0[1]), new Point2D(m_targ_1[0], m_targ_1[1]), m_attribute);
    }

    @Override
    public boolean isStateful()
    {
        return true;
    }

    @Override
    public boolean isRefreshing()
    {
        return false;
    }
}