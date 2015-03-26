package analytics;

import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChart {
	private String chartTitle;
	private String axisLabel;
	private String valueLabel;
	private XYSeriesCollection dataset;
	private ArrayList<XYSeries> series;
	private ArrayList<String> seriesNames;
	
	public LineChart(String title, String axisLabel, String valueLabel)
	{
		this.chartTitle=title;
		this.axisLabel=axisLabel;
		this.valueLabel=valueLabel;
	}
	
	public void addColName(String value)
	{
		if(this.seriesNames==null)
		{
			seriesNames=new ArrayList<String>();
		}
		seriesNames.add(value);
	}
	
	
	public void addToDataSet(float[] xValues,float[] yValues,String rowIdentifier)
    {
		if(dataset==null)
		{
			this.dataset=new XYSeriesCollection();
			this.series=new ArrayList<XYSeries>();
		}
		
		XYSeries obj=new XYSeries(rowIdentifier);
		int i=0;
		while(i<xValues.length)
		{
			obj.add(xValues[i],yValues[i]);
			i++;
		}
		series.add(obj);
		dataset.addSeries(obj);
    }
	
	public JFreeChart createChart() 
	{  
		JFreeChart chart = ChartFactory.createXYLineChart(
	            chartTitle,      // chart title
	            axisLabel,                      // x axis label
	            valueLabel,                      // y axis label
	            dataset,                  // data
	            PlotOrientation.VERTICAL,
	            true,                     // include legend
	            true,                     // tooltips
	            false                     // urls
	        );
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		XYPlot plot = chart.getXYPlot();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        plot.setRenderer(renderer);
		return chart;
	}
}
