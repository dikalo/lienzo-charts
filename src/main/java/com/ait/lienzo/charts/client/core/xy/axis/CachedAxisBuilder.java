package com.ait.lienzo.charts.client.core.xy.axis;

import com.ait.lienzo.charts.client.core.xy.XYChartData;
import com.ait.lienzo.charts.shared.core.types.AxisDirection;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CachedAxisBuilder<T> extends AxisBuilder<T> {
    
    private List<AxisLabel> labels;
    
    final private Map<String, List<AxisValue<T>>> values = new HashMap<>();
    
    public CachedAxisBuilder(XYChartData data, double chartSizeAttribute) {
        super(data, chartSizeAttribute);
        clear();
    }

    public CachedAxisBuilder(XYChartData data, double chartSizeAttribute, AxisDirection axisDirection) {
        super(data, chartSizeAttribute, axisDirection);
        clear();
    }

    protected abstract List<AxisLabel> buildLabels();

    protected abstract List<AxisValue<T>> buildValues(String modelProperty);

    @Override
    public List<AxisLabel> getLabels() {
        if (labels == null) 
        {
            labels = buildLabels();
        }
        return labels;
    }

    @Override
    public List<AxisValue<T>> getValues(String modelProperty) {
        if (!values.containsKey(modelProperty))
        {
            List<AxisValue<T>> _values = buildValues(modelProperty);
            values.put(modelProperty, _values);
        }
        return values.get(modelProperty);
    }
    
    @Override
    public void reload(XYChartData data, Collection currentSeries, double chartSizeAttribute) {
        super.reload(data, currentSeries, chartSizeAttribute);
        clear();
    }

    @Override
    public void reload(double chartSizeAttribute) {
        super.reload(chartSizeAttribute);
        clear();
    }

    protected void clear() {
        labels = null;
        values.clear();
    }
    
}
