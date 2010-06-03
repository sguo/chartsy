package org.chartsy.volumeoverlay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.utils.DefaultPainter;
import org.chartsy.main.utils.Range;
import org.openide.nodes.AbstractNode;

public class VolumeOverlay
        extends Overlay
        implements Serializable
{

    private static final long serialVersionUID = 2L;
    public static final String VOLUME = "volume";
    private OverlayProperties properties;

    public VolumeOverlay()
    {
        super();
        properties = new OverlayProperties();
    }

    public String getName()
    {
        return "Volume";
    }

    public String getLabel()
    {
        return properties.getLabel();
    }

    public String getPaintedLabel(ChartFrame cf)
    {
        DecimalFormat df = new DecimalFormat("###,###");
        String factor = df.format((int) getVolumeFactor(cf));
        return getLabel() + " x " + factor;
    }

    public Overlay newInstance()
    {
        return new VolumeOverlay();
    }

    public LinkedHashMap getHTML(ChartFrame cf, int i)
    {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat();
        df.applyPattern("###,###");
        String factor = df.format((int) getVolumeFactor(cf));
        df.applyPattern("###,##0.00");
        double[] values = getValues(cf, i);

        ht.put(getLabel() + " x " + factor, " ");
        if (values.length > 0)
        {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++)
            {
                ht.put(getFontHTML(colors[j], "Volume:"),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public void paint(Graphics2D g, ChartFrame cf, Rectangle bounds)
    {
        Dataset d = visibleDataset(cf, VOLUME);
        if (d != null)
        {
            Range range = getRange(cf, VOLUME);
            int height =  bounds.getSize().height/4;
            bounds.setLocation(bounds.getLocation().x, bounds.getLocation().y+height*3);
            bounds.setSize(bounds.getSize().width, height);
            Color colorVolume = new Color(properties.getColor().getRed(),properties.getColor().getGreen(),properties.getColor().getBlue(),properties.getAlpha());
            DefaultPainter.bar(g, cf, range, bounds, d, colorVolume);
        }
    }

    public void calculate()
    {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty())
        {
            Range range = new Range(0, initial.getMax(Dataset.VOLUME_PRICE));
            double factor = Math.pow(10, String.valueOf(Math.round(range.getUpperBound())).length() - 2);
            int count = initial.getItemsCount();
            Dataset d = Dataset.EMPTY(count);
            for (int i = 0; i < count; i++)
            {
                d.setDataItem(i, new DataItem(initial.getTimeAt(i), initial.getVolumeAt(i) / factor));
            }
            addDataset(VOLUME, d);
        }
    }

    public boolean hasZeroLine()
    {
        return true;
    }

    public boolean getZeroLineVisibility()
    {
        return properties.getZeroLineVisibility();
    }

    public Color getZeroLineColor()
    {
        return properties.getZeroLineColor();
    }

    public Stroke getZeroLineStroke()
    {
        return properties.getZeroLineStroke();
    }

    public boolean hasDelimiters()
    {
        return false;
    }

    public boolean getDelimitersVisibility()
    {
        return false;
    }

    public double[] getDelimitersValues()
    {
        return new double[]
                {
                };
    }

    public Color getDelimitersColor()
    {
        return null;
    }

    public Stroke getDelimitersStroke()
    {
        return null;
    }

    public Color[] getColors()
    {
        return new Color[]
                {
                    properties.getColor()
                };
    }

    public double[] getValues(ChartFrame cf)
    {
        Dataset d = visibleDataset(cf, VOLUME);
        if (d != null)
        {
            return new double[]
                    {
                        d.getLastClose()
                    };
        }
        return new double[]
                {
                };
    }

    public double[] getValues(ChartFrame cf, int i)
    {
        Dataset d = visibleDataset(cf, VOLUME);
        if (d != null)
        {
            return new double[]
                    {
                        d.getCloseAt(i)
                    };
        }
        return new double[]
                {
                };
    }

    public boolean getMarkerVisibility()
    {
        return properties.getMarker();
    }

    public AbstractNode getNode()
    {
        return new OverlayNode(properties);
    }

    private double getVolumeFactor(ChartFrame cf)
    {
        return Math.pow(10, String.valueOf(Math.round(cf.getChartData().getVisible().getMax(Dataset.VOLUME_PRICE))).length() - 1);
    }

    public String getPrice()
    {
        return VOLUME;
    }

    @Override
    public boolean isIncludedInRange()
    {
        return false;
    }
}