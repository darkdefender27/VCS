import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import analytics.AnalyticsEntryPoint;
import analytics.TimeSeriesChart;

import com.diff.core.Diff;
import com.diff.core.FileDiffResult;
import com.diff.core.MergeResult;
import com.diff.core.MergeResultItem;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Week;

public class EntryPoint extends JFrame {

	private static final long serialVersionUID = 1L;

	public EntryPoint(ChartPanel chartPanel) 
	{
		// super(applicationTitle);
		// // This will create the dataset
		// DefaultCategoryDataset dataset = createDataset();
		// // based on the dataset we create the chart
		// JFreeChart chart = createChart(dataset, chartTitle);
		// // we put the chart into a panel
		// ChartPanel chartPanel = new ChartPanel(chart);
		// // default size
		// chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		// // add it to our application
		this.setPreferredSize(new Dimension(500, 270));
		setContentPane(chartPanel);
	}

	/**
	 * Creates a sample dataset
	 */

	@SuppressWarnings("unused")
	private DefaultCategoryDataset createDataset() 
	{
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(1.0, "Row 1", "Column 1");
		// dataset.addValue(5.0, "Row 1", "Column 2");
		// dataset.addValue(3.0, "Row 1", "Column 3");
		dataset.addValue(2.0, "Row 2", "Column 1");
		// dataset.addValue(3.0, "Row 2", "Column 2");
		// dataset.addValue(2.0, "Row 2", "Column 3");
		return dataset;
	}

	/**
	 * Creates a chart
	 */

	@SuppressWarnings("unused")
	private JFreeChart createChart(CategoryDataset dataset, String title) {
		JFreeChart chart = ChartFactory.createBarChart(title, "temp", "value",
				(CategoryDataset) dataset);
		return chart;
	}

	public static void main(String[] args) 
	{
		/*
		 * EntryPoint demo = new EntryPoint("Comparison",
		 * "Which operating system are you using?"); demo.pack();
		 * demo.setVisible(true);
		 */
//		 TimeSeriesChart chart=new TimeSeriesChart("title","time","no of users");
//		 //float xArray[]={1f,2f,3f};
//		 //float yArray[]={50f,70f,65f};
//		 //chart.addToDataSet(xArray, yArray, "windows");
//		 //chart.addColName("Ubuntu");
//		 //chart.addColName("Mac");
//		 float xArrayNew[]={1f,2f,3f};
//		 float yArrayNew[]={7f,20f};
//		 RegularTimePeriod[] obj1=new Week[]{new Week(15,2015),new Week(16,2015)};
//		 chart.addToDataSet(obj1, yArrayNew, "temp",false,7);
//		 JFreeChart createdChart=chart.createChart();
//		 ChartPanel panel=new ChartPanel(createdChart);
//		 panel.setSize(new java.awt.Dimension(1000, 500));
//		 // add it to our application
//		 EntryPoint ep=new EntryPoint(panel);
//		 ep.setVisible(true);

		String file1Contents, file2Contents, file3Contents;
		file1Contents = readFileIntoString("C:/Users/Ambarish/Desktop/vcsdebug/1.txt");
		//file2Contents =readFileIntoString("C:/Users/Ambarish/Desktop/vcsdebug/2.txt");
		file2Contents = readFileIntoString("C:/Users/Ambarish/Desktop/project/1.txt");
		file3Contents = readFileIntoString("C:/Users/Ambarish/Desktop/project/VCS/1.txt");
		//System.out.println("here");
		//FileDiffResult result = null;
//		try 
//		{
//			Diff obj=new Diff();
//			result = obj.diff(file1Contents,file2Contents, null, false);
//			System.out.println(result.getLineResult().getNoOfLinesAdded()
//					+ " lines inserted ");
//			System.out.println((result.getLineResult().getNoOfLinesDeleted())
//					+ " lines deleted ");
//			System.out.println(result.getLineResult().getNoOfLinesUnmodified()
//					+ " lines unchanged ");
//		} catch (Exception e) {
//			System.out.println("here");
//		}
		String result=getMergeResult(file3Contents, file1Contents, file2Contents, "master", "master", "a0354f", "45utnkla2");
		System.out.println(result);
		//AnalyticsEntryPoint obj = new AnalyticsEntryPoint("C:/Users/Ambarish/Desktop/vcsdebug/");
	}
	
	public static String getMergeResult(String commonAncestor,String version1, String version2,String leftBranchName, String rightBranchName,String leftCommitId,String rightCommitId)
	{
		String retVal=null;
		Diff obj = new Diff();
		MergeResult mr = obj.merge(commonAncestor, version1, version2,null, false);
		if (!mr.isConflict()) 
		{
			retVal=mr.getDefaultMergedResult();
		}
		else
		{
			StringBuilder sb=new StringBuilder();
			int i = 0;
			while (i < mr.getMergeItems().size()) 
			{
				MergeResultItem mri = mr.getMergeItems().get(i);
				if (mri.getType() == MergeResultItem.Type.CONFLICT)
				{
					int j = 0;
					sb.append("====================================");
					sb.append(leftBranchName);
					sb.append("          ");
					sb.append(leftCommitId);
					sb.append(System.lineSeparator());
					
					while (j < mri.getLeftVersion().size()) 
					{
						sb.append(mri.getLeftVersion().get(j).getContent());
						sb.append(System.lineSeparator());
						j++;
					}
					
					sb.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
					sb.append(rightBranchName);
					sb.append("          ");
					sb.append(rightCommitId);
					sb.append(System.lineSeparator());
					j = 0;
					while (j < mri.getLeftVersion().size()) 
					{
						sb.append(mri.getRightVersion().get(j).getContent());
						sb.append(System.lineSeparator());
						j++;
					}
					sb.append("------------------------------------");
				}
				else
				{
					int k=0,max;
					max=mri.getLeftVersion().size();
					while(k<max)
					{
						sb.append(mri.getLeftVersion().get(k));
						sb.append(System.lineSeparator());
						k++;
					}
				}
				i++;
			}
			retVal=sb.toString();
		 }
		return retVal;
	}

	public static String readFileIntoString(String completeFileName) 
	{
		String retVal = null;
		try 
		{
			retVal = new String(Files.readAllBytes(Paths.get(completeFileName)));
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}

}
