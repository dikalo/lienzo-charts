
package com.ait.lienzo.charts.client.core.resizer;

import com.google.gwt.event.shared.GwtEvent;

public class ChartResizeAreaEvent extends GwtEvent<ChartResizeAreaEventHandler>
{
    public static Type<ChartResizeAreaEventHandler> TYPE = new Type<ChartResizeAreaEventHandler>();

    private boolean                             enteringResizeArea;

    public ChartResizeAreaEvent(final boolean enteringResizeArea)
    {
        this.enteringResizeArea = enteringResizeArea;
    }

    @Override
    public Type<ChartResizeAreaEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(ChartResizeAreaEventHandler handler)
    {
        handler.onChartResizeArea(this);
    }

    public boolean isEnteringResizeArea() {
        return enteringResizeArea;
    }
}