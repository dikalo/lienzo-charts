
package com.ait.lienzo.charts.client.core.animation;

import com.ait.lienzo.client.core.animation.*;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.types.NFastArrayList;

public class StackedTweeningAnimation extends TimedAnimation
{
    private final AnimationTweener        m_tweener;

    private NFastArrayList<NodeAnimation> m_animations;

    public StackedTweeningAnimation(final Node<?> node, final AnimationTweener tweener, double duration, IAnimationCallback callback)
    {
        super(duration, callback);

        m_animations = new NFastArrayList<NodeAnimation>();

        this.m_tweener = tweener;

        setNode(node);
    }

    protected final class NodeAnimation
    {
        private Node<?>                           node;

        private AnimationProperties               m_properties;

        private NFastArrayList<AnimationProperty> m_workingset;

        public NodeAnimation(Node<?> node, AnimationProperties m_properties)
        {
            this.node = node;

            this.m_properties = m_properties;
        }
    }

    public StackedTweeningAnimation add(final Node<?> node, final AnimationProperties properties)
    {
        m_animations.add(new NodeAnimation(node, properties));

        return this;
    }

    @Override
    public IAnimation doStart()
    {
        final int size = m_animations.size();

        for (int i = 0; i < size; i++)
        {
            NodeAnimation nodeAnimation = m_animations.get(i);

            doStart(nodeAnimation);
        }
        return super.doStart();
    }

    @Override
    public IAnimation doFrame()
    {
        final int size = m_animations.size();

        for (int i = 0; i < size; i++)
        {
            NodeAnimation nodeAnimation = m_animations.get(i);

            boolean draw = apply(nodeAnimation, getPercent());

            if (draw)
            {
                LayerRedrawManager.get().schedule(getNode().getLayer());
            }
        }
        return super.doFrame();
    }

    @Override
    public IAnimation doClose()
    {
        final int size = m_animations.size();

        for (int i = 0; i < size; i++)
        {
            NodeAnimation nodeAnimation = m_animations.get(i);

            doClose(nodeAnimation);
        }
        return super.doClose();
    }

    private IAnimation doStart(NodeAnimation nodeAnimation)
    {
        if ((null != nodeAnimation.m_properties) && (nodeAnimation.m_properties.size() > 0))
        {
            nodeAnimation.m_workingset = new NFastArrayList<AnimationProperty>();

            final Node<?> node = nodeAnimation.node;

            final int size = nodeAnimation.m_properties.size();

            for (int i = 0; i < size; i++)
            {
                AnimationProperty property = nodeAnimation.m_properties.get(i);

                if (null != property)
                {
                    if (property.isStateful())
                    {
                        property = property.copy();
                    }
                    if (null != property)
                    {
                        if (property.init(node))
                        {
                            nodeAnimation.m_workingset.add(property);
                        }
                    }
                }
            }
        }
        apply(nodeAnimation, 0.0);

        return this;
    }

    private IAnimation doClose(NodeAnimation nodeAnimation)
    {
        apply(nodeAnimation, 1.0);

        nodeAnimation.m_workingset = null;

        return this;
    }

    protected boolean apply(NodeAnimation nodeAnimation, double percent)
    {
        boolean draw = false;

        if (null != m_tweener)
        {
            percent = m_tweener.apply(percent);
        }
        if (null != nodeAnimation.m_workingset)
        {
            final int size = nodeAnimation.m_workingset.size();

            if (size > 0)
            {
                final Node<?> node = nodeAnimation.node;

                for (int i = 0; i < size; i++)
                {
                    boolean good = nodeAnimation.m_workingset.get(i).apply(node, percent);

                    draw = (draw || good);
                }
            }
        }
        return draw;
    }
}
