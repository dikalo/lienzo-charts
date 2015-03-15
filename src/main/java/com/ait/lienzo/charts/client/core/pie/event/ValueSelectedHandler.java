
package com.ait.lienzo.charts.client.core.pie.event;

import com.google.gwt.event.shared.EventHandler;

public interface ValueSelectedHandler extends EventHandler
{
    void onValueSelected(ValueSelectedEvent event);
}