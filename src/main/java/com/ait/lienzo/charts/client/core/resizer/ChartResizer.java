
package com.ait.lienzo.charts.client.core.resizer;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.LayerRedrawManager;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Arrow;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ArrowType;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;

public class ChartResizer extends Group
{
    private static final int             RECTANGLE_SIZE               = 30;

    private static final double          RECTANGLE_INITIA_ALPHA       = 0.2d;

    private static final double          RECTANGLE_ANIMATION_DURATION = 500;
    
    private static final double          SIZE_TEXT_FONT_SIZE          = 15;
    
    private static final String          SIZE_TEXT_FONT_FAMILY        = "Verdana";

    private static final String          SIZE_TEXT_FONT_STYLE         = "Bold";
    
    private double                       width;

    private double                       height;

    private int                          initialXPosition;

    private int                          initialYPosition;
    private static final NumberFormat    NUMBER_FORMAT                 = NumberFormat.getFormat("####");

    public ChartResizer(final double width, final double height)
    {
        build(width, height);
    }

    public Group build(final double width, final double height)
    {
        this.width = width;
        this.height = height;
        final double rectangleXPos = width - RECTANGLE_SIZE;
        final double rectangleYPos = height - RECTANGLE_SIZE;
        final Rectangle resizeRectangleButton = new Rectangle(RECTANGLE_SIZE, RECTANGLE_SIZE).setX(rectangleXPos).setY(rectangleYPos).setFillColor(ColorName.GREY).setDraggable(true).setAlpha(RECTANGLE_INITIA_ALPHA);
        final Rectangle resizeRectangle = new Rectangle(width, height).setX(0).setY(0).setFillColor(ColorName.GREY).setAlpha(0);
        final Arrow resizeArrow1 = new Arrow(new Point2D(width / 2, height / 2), new Point2D(width, height / 2), 0, 10, 10, 10, ArrowType.AT_END_TAPERED).setFillColor(ColorName.BLACK).setAlpha(0);
        final Arrow resizeArrow2 = new Arrow(new Point2D(width / 2, height / 2), new Point2D(width / 2, height), 0, 10, 10, 10, ArrowType.AT_END_TAPERED).setFillColor(ColorName.BLACK).setAlpha(0);
        final Arrow resizeArrow3 = new Arrow(new Point2D(width / 2, height / 2), new Point2D(0, height / 2), 0, 10, 10, 10, ArrowType.AT_END_TAPERED).setFillColor(ColorName.BLACK).setAlpha(0);
        final Arrow resizeArrow4 = new Arrow(new Point2D(width / 2, height / 2), new Point2D(width / 2, 0), 0, 10, 10, 10, ArrowType.AT_END_TAPERED).setFillColor(ColorName.BLACK).setAlpha(0);
        final Text sizeText = new Text(buildSizeText(width, height)).setFontSize(SIZE_TEXT_FONT_SIZE).setFontFamily(SIZE_TEXT_FONT_FAMILY).setFontStyle(SIZE_TEXT_FONT_STYLE).setFillColor(ColorName.BLACK);

        resizeRectangleButton.addNodeMouseEnterHandler(new NodeMouseEnterHandler()
        {
            @Override
            public void onNodeMouseEnter(NodeMouseEnterEvent event)
            {
                // Apply alphas.
                AnimationProperties animationProperties = new AnimationProperties();
                animationProperties.push(AnimationProperty.Properties.ALPHA(0.5));
                resizeRectangleButton.animate(AnimationTweener.LINEAR, animationProperties, RECTANGLE_ANIMATION_DURATION);
                resizeRectangle.animate(AnimationTweener.LINEAR, animationProperties, RECTANGLE_ANIMATION_DURATION);
                resizeArrow1.animate(AnimationTweener.LINEAR, animationProperties, RECTANGLE_ANIMATION_DURATION);
                resizeArrow2.animate(AnimationTweener.LINEAR, animationProperties, RECTANGLE_ANIMATION_DURATION);
                resizeArrow3.animate(AnimationTweener.LINEAR, animationProperties, RECTANGLE_ANIMATION_DURATION);
                resizeArrow4.animate(AnimationTweener.LINEAR, animationProperties, RECTANGLE_ANIMATION_DURATION);
                showSizeText(sizeText, ChartResizer.this.width, ChartResizer.this.height);
                sizeText.animate(AnimationTweener.LINEAR, animationProperties, RECTANGLE_ANIMATION_DURATION);

                // Fire the event for entering resize area.
                ChartResizer.this.fireEvent(new ChartResizeAreaEvent(true));
            }
        });

        resizeRectangleButton.addNodeMouseExitHandler(new NodeMouseExitHandler()
        {
            @Override
            public void onNodeMouseExit(NodeMouseExitEvent event)
            {
                // Apply alphas.
                AnimationProperties animationProperties = new AnimationProperties();
                animationProperties.push(AnimationProperty.Properties.ALPHA(RECTANGLE_INITIA_ALPHA));
                resizeRectangleButton.animate(AnimationTweener.LINEAR, animationProperties, RECTANGLE_ANIMATION_DURATION);

                // Apply alphas.
                AnimationProperties animationProperties2 = new AnimationProperties();
                animationProperties2.push(AnimationProperty.Properties.ALPHA(0));
                resizeRectangle.animate(AnimationTweener.LINEAR, animationProperties2, RECTANGLE_ANIMATION_DURATION);
                resizeArrow1.animate(AnimationTweener.LINEAR, animationProperties2, RECTANGLE_ANIMATION_DURATION);
                resizeArrow2.animate(AnimationTweener.LINEAR, animationProperties2, RECTANGLE_ANIMATION_DURATION);
                resizeArrow3.animate(AnimationTweener.LINEAR, animationProperties2, RECTANGLE_ANIMATION_DURATION);
                resizeArrow4.animate(AnimationTweener.LINEAR, animationProperties2, RECTANGLE_ANIMATION_DURATION);
                sizeText.animate(AnimationTweener.LINEAR, animationProperties2, RECTANGLE_ANIMATION_DURATION);

                // Fire the event for entering resize area.
                ChartResizer.this.fireEvent(new ChartResizeAreaEvent(false));
            }
        });

        resizeRectangleButton.addNodeDragStartHandler(new NodeDragStartHandler()
        {
            @Override
            public void onNodeDragStart(NodeDragStartEvent event)
            {
                initialXPosition = event.getX();
                initialYPosition = event.getY();
            }
        });

        resizeRectangleButton.addNodeDragEndHandler(new NodeDragEndHandler()
        {
            @Override
            public void onNodeDragEnd(NodeDragEndEvent event)
            {
                int currentX = event.getX();
                int currentY = event.getY();
                int incrementX = currentX - initialXPosition;
                int incrementY = currentY - initialYPosition;
                initialXPosition = currentX;
                initialYPosition = currentY;
                double finalWidth = ChartResizer.this.width + incrementX;
                double finalHeight = ChartResizer.this.height + incrementY;
                showSizeText(sizeText, finalWidth, finalHeight);
                
                // Animate the resize rectangle to its final position.
                AnimationProperties rectangleAnimationProperties = new AnimationProperties();
                rectangleAnimationProperties.push(AnimationProperty.Properties.X(finalWidth - RECTANGLE_SIZE));
                rectangleAnimationProperties.push(AnimationProperty.Properties.Y(finalHeight - RECTANGLE_SIZE));
                //IAnimationHandle rectangleAnimationHandle = resizeRectangleButton.animate(AnimationTweener.LINEAR, rectangleAnimationProperties, ANIMATION_DURATION);

                // Fire the resize event with apply flag set (final size).
                ChartResizer.this.fireEvent(new ChartResizeEvent(finalWidth, finalHeight, true));

                ChartResizer.this.width = finalWidth;
                ChartResizer.this.height = finalHeight;
            }
        });

        resizeRectangleButton.addNodeDragMoveHandler(new NodeDragMoveHandler() {
            @Override
            public void onNodeDragMove(NodeDragMoveEvent event) {
                int currentX = event.getX();
                int currentY = event.getY();
                int incrementX = currentX - initialXPosition;
                int incrementY = currentY - initialYPosition;
                double finalWidth = ChartResizer.this.width + incrementX;
                double finalHeight = ChartResizer.this.height + incrementY;
                resizeRectangle.setWidth(finalWidth).setHeight(finalHeight);
                Point2D start = new Point2D(finalWidth / 2, finalHeight / 2);
                resizeArrow1.setStart(start).setEnd(new Point2D(finalWidth, finalHeight / 2));
                resizeArrow2.setStart(start).setEnd(new Point2D(finalWidth / 2, finalHeight));
                resizeArrow3.setStart(start).setEnd(new Point2D(0, finalHeight / 2));
                resizeArrow4.setStart(start).setEnd(new Point2D(finalWidth / 2, 0));
                showSizeText(sizeText, finalWidth, finalHeight);
                
                // Fire the resize event with apply flag not set (not final size yet).
                ChartResizer.this.fireEvent(new ChartResizeEvent(finalWidth, finalHeight, false));

                LayerRedrawManager.get().schedule(resizeRectangle.getLayer());
            }
        });
        this.add(resizeRectangle);
        this.add(resizeRectangleButton);
        this.add(resizeArrow1);
        this.add(resizeArrow2);
        this.add(resizeArrow3);
        this.add(resizeArrow4);
        this.add(sizeText);
        
        return this;
    }
    
    private static void showSizeText(final Text text, final double w, final double h) {
        final String _t = buildSizeText(w, h);
        text.setText(_t);
        final double tw = text.getBoundingBox().getWidth();
        final double th = text.getBoundingBox().getHeight();
        final double tx = w/2 - tw/2;
        final double ty = h/2 - th/2;
        text.setX(tx).setY(ty).moveToTop();
    }

    private static String buildSizeText(final double w, final double h) {
        return NUMBER_FORMAT.format(w) + "/" + NUMBER_FORMAT.format(h);
    }

    public HandlerRegistration addChartResizeEventHandler(ChartResizeEventHandler handler)
    {
        return addEnsureHandler(ChartResizeEvent.TYPE, handler);
    }

    public HandlerRegistration addChartResizeAreaEventHandler(ChartResizeAreaEventHandler handler)
    {
        return addEnsureHandler(ChartResizeAreaEvent.TYPE, handler);
    }
    
}
