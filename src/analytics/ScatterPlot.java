package analytics;

import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScatterPlot 
{
	private String chartTitle;
	private String axisLabel;
	private String valueLabel;
	
	private XYSeriesCollection dataset=null;
	private ArrayList<XYSeries> series;
	
	public ScatterPlot(String chartTitle, String axisLabel, String valueLabel)
	{
		this.chartTitle=chartTitle;
		this.axisLabel=axisLabel;
		this.valueLabel=valueLabel;
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
		JFreeChart chart = ChartFactory.createScatterPlot(chartTitle, axisLabel, valueLabel, dataset);
//		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//		XYPlot plot = chart.getXYPlot();
//        renderer.setSeriesLinesVisible(0, true);
//        renderer.setSeriesShapesVisible(1, true);
//        plot.setRenderer(renderer);
		return chart;
	}
}
