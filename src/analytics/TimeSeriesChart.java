package analytics;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class TimeSeriesChart 
{
	private String chartTitle;
	private String axisLabel;
	private String valueLabel;
	
	private TimeSeries series;
	private TimeSeriesCollection dataset;
	
	public TimeSeriesChart(String chartTitle, String axisLabel, String valueLabel)
	{
		this.chartTitle=chartTitle;
		this.axisLabel=axisLabel;
		this.valueLabel=valueLabel;
	}
	
	public void addToDataSet(RegularTimePeriod[] periods,float[] values,String rowIdentifier,boolean movingAvgReqd,int periodLength)
    {
		if(dataset==null || series==null)
		{
			this.dataset=new TimeSeriesCollection();
			series=new TimeSeries("");
		}
		int i=0;
		series=new TimeSeries(rowIdentifier);
		while(i<periods.length)
		{
			series.add(periods[i], values[i]);
			i++;
		}
		dataset.addSeries(series);
		if(movingAvgReqd)
		{
			TimeSeries mav = MovingAverage.createMovingAverage(series, "moving avg "+rowIdentifier, periodLength, 0);
			this.dataset.addSeries(mav);
		}
    }
	
	public JFreeChart createChart() 
	{   
        JFreeChart chart = ChartFactory.createTimeSeriesChart(this.chartTitle, this.axisLabel, this.valueLabel, this.dataset);
        return chart;    
    }
}