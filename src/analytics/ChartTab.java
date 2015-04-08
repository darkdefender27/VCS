package analytics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JPanel;

import org.jfree.data.time.Week;

import objects.VCSCommit;

import vcs.Constants;
import vcs.Operations;

public class ChartTab {
	public String xAxisParameter;
	public String yAxisParameter;
	public String whereClauseParameter;
	public String whereClauseValue;
	public String workingDir;
	public ChartTab(String xAxisParameter, String yAxisParameter, String whereClauseParameter, String whereClauseValue, String workingDir)
	{
		this.xAxisParameter=xAxisParameter;
		this.yAxisParameter=yAxisParameter;
		this.whereClauseParameter=whereClauseParameter;
		this.whereClauseValue=whereClauseValue;
		this.workingDir=workingDir;
	}

	public JPanel getChartPanel() 
	{
		// TODO Auto-generated method stub
		JPanel panel=new JPanel();
		GridLayout layout = new GridLayout(1, 2);
		panel.setSize(new Dimension(500, 300));
		panel.setLayout(layout);
		panel.setBackground(Color.BLACK);
		Operations obj=new Operations();
		if(xAxisParameter.equals("no of commits") && yAxisParameter.equals("week"))
		{
			if((whereClauseParameter.equals("branch") && whereClauseValue.equals("all")) || (whereClauseParameter.equals("developer") && whereClauseValue.equals("all")))
			{
				//timeseries chart
				ArrayList<String> branches=new ArrayList<String>();
				branches=getBranches();
				int i=0,max=branches.size();
				while(i<max)
				{
					try 
					{
						VCSCommit head=obj.getBranchHead(workingDir, branches.get(i));
						Queue<VCSCommit> que=new LinkedList<VCSCommit>();
						ArrayList<Week> weeks=new ArrayList<Week>();
						Week w=new Week();
					}
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					i++;
				}
			}
			else
			{
				
			}
		}
		else if(xAxisParameter.equals("no of commits") && yAxisParameter.equals("developer"))
		{
			
		}
		else if(xAxisParameter.equals("no of commits") && yAxisParameter.equals("branch"))
		{
			
		}
		else if(xAxisParameter.equals("no of lines added & deleted") && yAxisParameter.equals("week"))
		{
			
		}
		else if(xAxisParameter.equals("no of lines added & deleted") && yAxisParameter.equals("branch"))
		{
			
		}
		else if(xAxisParameter.equals("no of lines added & deleted") && yAxisParameter.equals("developer"))
		{
			
		}
		else if(xAxisParameter.equals("no of lines added & deleted") && yAxisParameter.equals("commit"))
		{
			
		}
		else if(xAxisParameter.equals("no of developers") && yAxisParameter.equals("branch"))
		{
			
		}
		//get chart n set
		//panel.add(chart);
		
		return panel;
	}
	
	
	private ArrayList<String> getBranches() {
		// TODO Auto-generated method stub
		String branchFolder= workingDir +"/" +Constants.VCSFOLDER +"/" +Constants.BRANCH_FOLDER;
		File f=new File(branchFolder);
		ArrayList<String> branchesList =new ArrayList<String>();
		if(f.exists() && f.isDirectory())
		{
			try 
			{
				File[] branches=f.listFiles();
				int i=0,max=branches.length;
				while(i<max)
				{
					if(!branchesList.contains(branches[i].getName()))
					{
						branchesList.add(branches[i].getName());
					}
					i++;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return branchesList;
	}

	private ArrayList<String> getDevelopers()
	{
		String devListFile= workingDir +"/" +Constants.VCSFOLDER +"/" +Constants.DEVELOPERS_FILE;
		File f=new File(devListFile);
		ArrayList<String> committerList =new ArrayList<String>();
		if(f.exists())
		{
			try 
			{
				BufferedReader br =new BufferedReader(new FileReader(f));
				String line=null;
				while((line=br.readLine())!=null)
				{
					if(!committerList.contains(line))
					{
						committerList.add(line);
					}
				}
				br.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return committerList;
	}
}
