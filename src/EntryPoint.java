import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.diff.core.Diff;
import com.diff.core.FileDiffResult;
import com.diff.core.MergeResult;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import analytics.ScatterPlot;

public class EntryPoint extends JFrame 
{
	

  private static final long serialVersionUID = 1L;

  public EntryPoint(ChartPanel chartPanel) 
  {
//        super(applicationTitle);
//        // This will create the dataset 
//        DefaultCategoryDataset dataset = createDataset();
//        // based on the dataset we create the chart
//        JFreeChart chart = createChart(dataset, chartTitle);
//        // we put the chart into a panel
//        ChartPanel chartPanel = new ChartPanel(chart);
//        // default size
//        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
//        // add it to our application
	  this.setPreferredSize(new Dimension(500,270));
        setContentPane(chartPanel);
    }
    
    
/**
     * Creates a sample dataset 
     */

    @SuppressWarnings("unused")
	private  DefaultCategoryDataset createDataset() 
    {
    	DefaultCategoryDataset dataset=new DefaultCategoryDataset();
    	dataset.addValue(1.0, "Row 1", "Column 1");
    	//dataset.addValue(5.0, "Row 1", "Column 2");
    	//dataset.addValue(3.0, "Row 1", "Column 3");
    	dataset.addValue(2.0, "Row 2", "Column 1");
    	//dataset.addValue(3.0, "Row 2", "Column 2");
    	//dataset.addValue(2.0, "Row 2", "Column 3");
    	return dataset;
    }
    
    
/**
     * Creates a chart
     */

    @SuppressWarnings("unused")
	private JFreeChart createChart(CategoryDataset dataset, String title) 
    {
    	JFreeChart chart = ChartFactory.createBarChart(title, "temp", "value", (CategoryDataset) dataset);
        return chart;
    }
    
    public static void EntryPoint_Main(String[] args) 
    {
        /*EntryPoint demo = new EntryPoint("Comparison", "Which operating system are you using?");
        demo.pack();
        demo.setVisible(true);*/
    	ScatterPlot chart=new ScatterPlot("title","time","no of users");
    	//float xArray[]={1f,2f,3f};
    	//float yArray[]={50f,70f,65f};
    	//chart.addToDataSet(xArray, yArray, "windows");
    	//chart.addColName("Ubuntu");
    	//chart.addColName("Mac");
    	float xArrayNew[]={1f,2f,3f};
    	float yArrayNew[]={33f,40f,12f};
    	//RegularTimePeriod[] obj1=new Week[]{new Week(0,2015),new Week(1,2015),new Week(2,2015)};
    	chart.addToDataSet(xArrayNew, yArrayNew, "temp");
    	JFreeChart createdChart=chart.createChart();
    	ChartPanel panel=new ChartPanel(createdChart);
    	panel.setSize(new java.awt.Dimension(1000, 500));
        // add it to our application
        EntryPoint ep=new EntryPoint(panel);
        ep.setVisible(true);
    }
} 
