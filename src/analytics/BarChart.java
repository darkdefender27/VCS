package analytics;

import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarChart 
{
	private String chartTitle;
	private String axisLabel;
	private String valueLabel;
	
	private DefaultCategoryDataset dataset=null;
	
	private ArrayList<String> colNames;
	private int noOfRows=0;
	private int noOfCols=0;
	
	//private String rowIdentifier="Row ";
	
	public BarChart(String chartTitle,String axisLabel,String valueLabel)
	{
        // based on the dataset we create the chart
		this.chartTitle=chartTitle;
		this.axisLabel=axisLabel;
		this.valueLabel=valueLabel;
        //JFreeChart chart = createChart();
        // we put the chart into a panel
        //ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        //chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
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
	
	public void addToDataSet(float[] values,String rowIdentifier)
    {
		if(dataset==null)
		{
			this.dataset=new DefaultCategoryDataset();
		}
		if(this.dataset!=null)
		{
			int i=0;
			while(i<this.noOfCols)
			{
				this.dataset.addValue(values[i], rowIdentifier, colNames.get(i));
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
				this.dataset.removeRow(rowNum);
			}
		}
	}
	
	public void removeFromDataSet(String rowKey)
	{
		if(this.dataset!=null)
		{
			this.dataset.removeRow(rowKey);
		}
	}
	
	public JFreeChart createChart() 
	{   
        JFreeChart chart = ChartFactory.createBarChart(chartTitle, axisLabel, valueLabel, (CategoryDataset) this.dataset);
        return chart;    
    }
}
