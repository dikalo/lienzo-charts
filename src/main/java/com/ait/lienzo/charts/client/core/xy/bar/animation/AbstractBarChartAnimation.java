
package com.ait.lienzo.charts.client.core.xy.bar.animation;

import java.util.List;
import java.util.Map;

import com.ait.lienzo.charts.client.core.animation.AbstractChartAnimation;
import com.ait.lienzo.charts.client.core.legend.ChartLegend;
import com.ait.lienzo.charts.client.core.xy.XYChartSeries;
import com.ait.lienzo.charts.client.core.xy.axis.AxisBuilder;
import com.ait.lienzo.charts.client.core.xy.axis.AxisLabel;
import com.ait.lienzo.charts.client.core.xy.axis.AxisValue;
import com.ait.lienzo.charts.client.core.xy.bar.BarChart;
import com.ait.lienzo.charts.client.core.xy.bar.BarChartLabel;
import com.ait.lienzo.charts.client.core.xy.bar.BarChartLabelFormatter;
import com.ait.lienzo.charts.shared.core.types.LabelsPosition;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.IColor;

public abstract class AbstractBarChartAnimation extends AbstractChartAnimation
{

    public AbstractBarChartAnimation(final BarChart barChart, final double chartWidth, final double chartHeight, AnimationTweener tweener, final double duration, final IAnimationCallback callback)
    {
        super(barChart, chartWidth, chartHeight, tweener, duration, callback);
    }

    protected BarChart getBarChart()
    {
        return (BarChart) getNode();
    }

    protected boolean isVertical()
    {
        return getBarChart().isVertical();
    }

    protected void calculate(final double w, final double h)
    {
        super.calculate(w, h);

        calculateCategoriesAxisTitle(w, h);
        calculateValuesAxisTitle(w, h);
        calculateCategoriesAxisIntervals(w, h);
        calculateValuesAxisIntervals(w, h);
        calculateValues(w, h);
    }

    protected void calculateCategoriesAxisTitle(final double w, final double h)
    {
        // Category axis title.
        if (!getBarChart().getCategoriesAxisTitle().isEmpty())
        {
            final Double ctx = isVertical() ? w / 2 : null;
            final Double cty = isVertical() ? 30d : h / 2;
            doAnimateCategoriesAxisTitle(getBarChart().getCategoriesAxisTitle().get(0), ctx, cty);
        }
    }

    protected abstract void doAnimateCategoriesAxisTitle(final Text categoriesAxisTitle, final Double x, final Double y);

    protected void calculateValuesAxisTitle(final double w, final double h)
    {
        // Values axis title.
        if (!getBarChart().getValuesAxisTitle().isEmpty())
        {
            final Double vtx = isVertical() ? null : w / 2;
            final Double vty = isVertical() ? h / 2 : 30d;
            doAnimateValuesAxisTitle(getBarChart().getValuesAxisTitle().get(0), vtx, vty);
        }
    }

    protected abstract void doAnimateValuesAxisTitle(final Text valuesAxisTitle, final Double x, final Double y);

    protected void calculateCategoriesAxisIntervals(final double w, final double h)
    {
        if (getBarChart().isShowCategoriesLabels())
        {
            final double ml = getBarChart().getMarginLeft();
            final double mr = getBarChart().getMarginRight();
            final double mt = getBarChart().getMarginTop();
            final double mb = getBarChart().getMarginBottom();
            final List<BarChartLabel> seriesLabels = getBarChart().getSeriesLabels();
            final AxisBuilder categoriesAxisBuilder = getBarChart().getCategoriesAxisBuilder();
            final List<AxisLabel> labels = categoriesAxisBuilder.getLabels();
            final int lsize = labels.size();
            final LabelsPosition clp = getBarChart().getCategoriesAxisLabelsPosition();

            if (labels != null && !labels.isEmpty())
            {
                // Check max labels size.
                final double maxLabelWidth = isVertical() ? w / lsize : (!clp.equals(LabelsPosition.RIGHT) ? ml : mr);
                final double maxLabelHeight = isVertical() ? (!clp.equals(LabelsPosition.TOP) ? mb : mt) : h / lsize;
                final BarChartLabelFormatter categoriesLabelFormatter = new BarChartLabelFormatter(seriesLabels);

                // Apply format to the labels.
                categoriesLabelFormatter.format(maxLabelWidth, maxLabelHeight);

                for (int i = 0; i < labels.size(); i++)
                {
                    final AxisLabel label = labels.get(i);
                    final double position = label.getPosition();
                    final BarChartLabel chartLabel = seriesLabels.get(i);

                    if (isVertical())
                    {
                        final double labelWidth = chartLabel.getLabelWidth();
                        //TODO: Use ComposedTweeningAnimation#add()
                        doAnimateCategoriesAxisIntervals(chartLabel, label, position - labelWidth / 2, 10d);
                    }
                    else
                    {
                        double xPos = 0;
                        if (!clp.equals(LabelsPosition.RIGHT))
                        {
                            // Left.
                            final double margin = ml;
                            final double lw = chartLabel.getLabelWidth();
                            xPos = (lw + 5 > margin) ? 0 : margin - lw - 5;
                        }
                        else
                        {
                            // Right.
                            xPos = 5;
                        }
                        //TODO: Use ComposedTweeningAnimation#add()
                        doAnimateCategoriesAxisIntervals(chartLabel, label, xPos, position - chartLabel.getLabelHeight() / 2);
                    }
                }
            }
            else
            {
                seriesLabels.clear();
            }
        }
    }

    protected abstract void doAnimateCategoriesAxisIntervals(final BarChartLabel chartLabel, final AxisLabel axisLabel, final Double x, final Double y);

    protected void calculateValuesAxisIntervals(final double w, final double h)
    {
        final double ml = getBarChart().getMarginLeft();
        final double mr = getBarChart().getMarginRight();
        final double mt = getBarChart().getMarginTop();
        final double mb = getBarChart().getMarginBottom();
        final List<Line> valuesAxisIntervals = getBarChart().getValuesAxisIntervals();
        final List<BarChartLabel> valuesLabels = getBarChart().getValuesLabels();
        final AxisBuilder valuesAxisBuilder = getBarChart().getValuesAxisBuilder();

        final List<AxisLabel> labels = valuesAxisBuilder.getLabels();
        if (labels != null && !labels.isEmpty())
        {
            final int lsize = labels.size();
            final LabelsPosition vlp = getBarChart().getValuesAxisLabelsPosition();

            final double maxLabelWidth = isVertical() ? (vlp.equals(LabelsPosition.LEFT) ? ml : mr) : w / 2;
            final double maxLabelHeight = isVertical() ? h / lsize : (vlp.equals(LabelsPosition.TOP) ? mt : mb);
            final BarChartLabelFormatter valuesLabelFormatter = new BarChartLabelFormatter(valuesLabels);

            // Apply format to the labels.
            valuesLabelFormatter.format(maxLabelWidth, maxLabelHeight);

            for (int i = 0; i < labels.size(); i++)
            {
                final AxisLabel label = labels.get(i);
                final double position = label.getPosition();

                final double p00 = isVertical() ? 0 : position;
                final double p01 = isVertical() ? position : 0;
                final double p10 = isVertical() ? w : position;
                final double p11 = isVertical() ? position : h;
                doAnimateValuesAxisIntervals(valuesAxisIntervals.get(i), p00, p01, p10, p11);

                if (getBarChart().isShowValuesLabels())
                {
                    BarChartLabel chartLabel = valuesLabels.get(i);

                    if (isVertical())
                    {
                        double xPos = 0;
                        if (vlp.equals(LabelsPosition.LEFT))
                        {
                            // Left.
                            final double marginLeft = ml;
                            final double lw = chartLabel.getLabelWidth();
                            xPos = (lw + 5 > marginLeft) ? 0 : marginLeft - lw - 5;
                        }
                        else
                        {
                            // Right.
                            xPos = 5;
                        }
                        doAnimateValuesAxisIntervals(chartLabel, label, xPos, position - 5);
                    }
                    else
                    {
                        double yPos = 0;
                        if (vlp.equals(LabelsPosition.TOP))
                        {
                            // Top.
                            final double marginTop = mt;
                            final double lh = chartLabel.getLabelHeight();
                            yPos = (lh + 5 > marginTop) ? 0 : marginTop - lh - 5;
                        }
                        else
                        {
                            // Bottom.
                            yPos = 5;
                        }
                        doAnimateValuesAxisIntervals(chartLabel, label, position, yPos);
                    }

                }
            }
        }
    }

    protected abstract void doAnimateValuesAxisIntervals(Line valueAxisInterval, final double p00, final double p01, final double p10, final double p11);

    protected abstract void doAnimateValuesAxisIntervals(final BarChartLabel chartLabel, final AxisLabel axisLabel, final Double x, final Double y);

    protected void calculateValues(final double w, final double h)
    {
        final AxisBuilder categoriesAxisBuilder = getBarChart().getCategoriesAxisBuilder();
        XYChartSeries[] series = getBarChart().getData().getSeries();
        final ChartLegend legend = getBarChart().getChartLegend();

        // Find removed series in order to remove bar rectangle instances.
        for (String removedSerieName : categoriesAxisBuilder.getDataSummary().getRemovedSeries())
        {
            // TODO: Remove serie values should be done only in a BarChartReloadAnimation?
            getBarChart().removeSeriesValues(removedSerieName);
            if (legend != null) legend.remove(removedSerieName).build();
        }
        categoriesAxisBuilder.getDataSummary().getRemovedSeries().clear();

        // Iterate over all series.
        for (int numSerie = 0; numSerie < series.length; numSerie++)
        {
            final XYChartSeries serie = series[numSerie];
            if (serie != null)
            {
                // If a new serie is added, draw new bar rectangle instances.
                boolean isSeriesNew = false;
                if (categoriesAxisBuilder.getDataSummary().getAddedSeries().contains(serie.getName()))
                {
                    // TODO: Build serie values should be done only in a BarChartReloadAnimation?
                    getBarChart().buildSeriesValues(serie, numSerie);
                    if (legend != null) legend.add(new ChartLegend.ChartLegendEntry(serie.getName(), serie.getColor())).build();
                    categoriesAxisBuilder.getDataSummary().getAddedSeries().remove(serie.getName());
                    isSeriesNew = true;
                }
                if (isVertical()) calculateValuesForVertical(serie, numSerie, w, h, isSeriesNew);
                else calculateValuesForHorizontal(serie, numSerie, w, h, isSeriesNew);
            }
        }
    }

    protected AbstractBarChartAnimation calculateValuesForVertical(final XYChartSeries serie, final int numSerie, final Double width, final Double height, boolean isSeriesNew)
    {
        final double ml = getBarChart().getMarginLeft();
        final double mr = getBarChart().getMarginRight();
        final double mt = getBarChart().getMarginTop();
        final double mb = getBarChart().getMarginBottom();
        final Map<String, List<Rectangle>> seriesValues = getBarChart().getSeriesValues();
        final AxisBuilder categoriesAxisBuilder = getBarChart().getCategoriesAxisBuilder();
        final AxisBuilder valuesAxisBuilder = getBarChart().getValuesAxisBuilder();
        XYChartSeries[] series = getBarChart().getData().getSeries();

        // Rebuild bars for serie values
        List<AxisValue> valuesAxisValues = valuesAxisBuilder.getValues(serie.getValuesAxisProperty());
        List<AxisValue> categoryAxisValues = categoriesAxisBuilder.getValues(getBarChart().getData().getCategoryAxisProperty());
        List<Rectangle> bars = seriesValues.get(serie.getName());

        if (categoryAxisValues != null && categoryAxisValues.size() > 0)
        {
            for (int i = 0; i < categoryAxisValues.size(); i++)
            {
                AxisValue categoryAxisvalue = categoryAxisValues.get(i);
                AxisValue valueAxisvalue = valuesAxisValues.get(i);
                double yAxisValuePosition = valueAxisvalue.getPosition();

                // Obtain width and height values for the bar.
                double barHeight = yAxisValuePosition;
                double barWidth = getWithForBar(width, series.length, categoryAxisValues.size());
                if (barWidth <= 0) barWidth = 1;

                // Calculate bar positions.
                double y = height - barHeight;
                double x = (barWidth * series.length * i) + (barWidth * numSerie) + (getBarChart().getValuesAxis().getSegments() * (i + 1));
                double alpha = 1d;

                // If current bar is not in Y axis intervals (max / min values), resize it and apply an alpha.
                boolean isOutOfChartArea = y < 0;
                if (isOutOfChartArea)
                {
                    alpha = 0.1d;
                    barHeight = height;
                    y = 0;
                }

                // Obtain the shape instance, add mouse handlers and reposition/resize it.
                final Rectangle barObject = bars.get(i);
                barObject.moveToTop();
                barObject.setDraggable(true);

                double lastBarWidth = barWidth - BarChart.BAR_SEPARATION;
                if (lastBarWidth > BarChart.BAR_MAX_SIZE)
                {
                    lastBarWidth -= barWidth * BarChart.BAR_MAX_SIZE_PROPORTION;
                    x += (barWidth / 2 - lastBarWidth / 2);
                }
                doAnimateValues(barObject, x, y, lastBarWidth, barHeight, serie.getColor(), alpha, isSeriesNew);
            }
        }

        return this;
    }

    protected double getWithForBar(final double chartWidth, final int numSeries, final int valuesCount)
    {
        // If exist more than one serie, and no stacked attribute is set, split each serie bar into the series count value.
        return getAvailableWidth(chartWidth, valuesCount) / valuesCount / numSeries;
    }

    protected double getAvailableWidth(final double chartWidth, final int valuesCount)
    {
        int yAxisDivisions = getBarChart().getValuesAxis().getSegments();
        return chartWidth - (yAxisDivisions * (valuesCount + 1));
    }

    protected AbstractBarChartAnimation calculateValuesForHorizontal(final XYChartSeries serie, final int numSerie, Double width, final Double height, boolean isSeriesNew)
    {
        final Map<String, List<Rectangle>> seriesValues = getBarChart().getSeriesValues();
        final AxisBuilder categoriesAxisBuilder = getBarChart().getCategoriesAxisBuilder();
        final AxisBuilder valuesAxisBuilder = getBarChart().getValuesAxisBuilder();
        XYChartSeries[] series = getBarChart().getData().getSeries();

        // Rebuild bars for serie values
        List<AxisValue> yAxisValues = categoriesAxisBuilder.getValues(getBarChart().getData().getCategoryAxisProperty());
        List<AxisValue> xAxisValues = valuesAxisBuilder.getValues(serie.getValuesAxisProperty());
        List<AxisLabel> xAxisLabels = valuesAxisBuilder.getLabels();
        List<Rectangle> bars = seriesValues.get(serie.getName());

        if (yAxisValues != null && yAxisValues.size() > 0)
        {
            for (int i = 0; i < yAxisValues.size(); i++)
            {
                AxisValue xAxisvalue = xAxisValues.get(i);
                double xAxisValuePosition = xAxisvalue.getPosition();

                // Obtain width and height values for the bar.
                int valuesSize = yAxisValues.size();
                double barWidth = xAxisValuePosition;
                double barHeight = getHeightForBar(height, series.length, valuesSize);
                if (barHeight <= 0) barHeight = 1;

                // Calculate bar positions.
                double x = 0;
                double y = (barHeight * series.length * i) + (barHeight * numSerie);
                double alpha = 1d;

                // If current bar is not in Y axis intervals (max / min values), resize it and apply an alpha.
                double lastXIntervalPosition = xAxisLabels.get(xAxisLabels.size() - 1).getPosition();
                boolean isOutOfChartArea = barWidth > lastXIntervalPosition;
                if (isOutOfChartArea)
                {
                    alpha = 0.1d;
                    barWidth = width;
                }

                // Obtain the shape instance, add mouse handlers and reposition/resize it.
                final Rectangle barObject = bars.get(i);
                barObject.setDraggable(true);

                double lastBarHeight = barHeight - BarChart.BAR_SEPARATION;
                if (lastBarHeight > BarChart.BAR_MAX_SIZE)
                {
                    lastBarHeight -= barHeight * BarChart.BAR_MAX_SIZE_PROPORTION;
                    y += (barHeight / 2 - lastBarHeight / 2);
                }
                doAnimateValues(barObject, x, y, barWidth, lastBarHeight, serie.getColor(), alpha, isSeriesNew);
            }
        }
        return this;
    }

    protected double getHeightForBar(final double chartHeight, final int numSeries, final int valuesCount)
    {
        // If exist more than one serie, and no stacked attribute is set, split each serie bar into the series count value.
        return chartHeight / valuesCount / numSeries;
    }

    protected abstract void doAnimateValues(final Rectangle bar, final Double x, final Double y, final Double w, final Double h, IColor color, final Double alpha, final boolean isSeriesNew);
}