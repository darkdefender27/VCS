package analytics;

import java.awt.Font;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class PieChart 
{
	private String chartTitle;
	//private String axisLabel;
	//private String valueLabel;
	
	private DefaultPieDataset dataset=null;
	
	private ArrayList<String> colNames;
	private int noOfRows=0;
	private int noOfCols=0;

	public PieChart(String chartTitle)
	{
		this.chartTitle=chartTitle;
		//this.axisLabel=axisLabel;
		//this.valueLabel=valueLabel;
	}
	
	public void addColName(String value)
	{
		if(this.colNames==null)
		{
			colNames=new ArrayList<String>();
		}
		colNames.add(value);
		noOfCols++;
	}
	
	public void addToDataSet(float[] values)
    {
		if(dataset==null)
		{
			this.dataset=new DefaultPieDataset();
		}
		if(this.dataset!=null)
		{
			int i=0;
			while(i<this.noOfCols)
			{
				this.dataset.setValue(colNames.get(i),values[i]);
				i++;
			}
			this.noOfRows++;
		}
    	//dataset.addValue(1.0, "Row 1", "Column 1");
    	//dataset.addValue(5.0, "Row 1", "Column 2");
    	//dataset.addValue(3.0, "Row 1", "Column 3");
    	//dataset.addValue(2.0, "Row 2", "Column 1");
    	//dataset.addValue(3.0, "Row 2", "Column 2");
    	//dataset.addValue(2.0, "Row 2", "Column 3");
    }
	
	public void removeFromDataSet(int rowNum)
	{
		if(this.dataset!=null)
		{
			if(rowNum<noOfRows)
			{
				this.dataset.remove(rowNum);
			}
		}
	}
	
	public void removeFromDataSet(String rowKey)
	{
		if(this.dataset!=null)
		{
			this.dataset.remove(rowKey);
		}
	}
	
	public JFreeChart createChart() 
	{   
		JFreeChart chart = ChartFactory.createPieChart(
	            chartTitle,  // chart title
	            dataset,             // data
	            true,               // include legend
	            true,
	            false
	        );

	        PiePlot plot = (PiePlot) chart.getPlot();
	        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
	        plot.setNoDataMessage("No data available");
	        plot.setCircular(false);
	        plot.setLabelGap(0.02);
	        return chart;
    }

	public void addToDataSet(Object[] values, String string) {
		// TODO Auto-generated method stub
		if(dataset==null)
		{
			this.dataset=new DefaultPieDataset();
		}
		if(this.dataset!=null)
		{
			int i=0;
			while(i<this.noOfCols)
			{
				this.dataset.setValue(colNames.get(i),(Number)values[i]);
				i++;
			}
			this.noOfRows++;
		}
	}
}
