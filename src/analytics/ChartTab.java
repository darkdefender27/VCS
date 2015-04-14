package analytics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
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
		PieChart extraChart;
		JFreeChart jfChart=null;
		JFreeChart extraJFChhart=null;
		if(xAxisParameter.equals("no of commits") && yAxisParameter.equals("week"))              //=============================================================
		{
			
			//Bar chart
			ArrayList<String> branches=new ArrayList<String>();
			branches=getBranches();
			int i=0,max=branches.size();
			HashMap<Week, Integer> hm=new HashMap<Week, Integer>();
			Queue<VCSCommit> que=new LinkedList<VCSCommit>();
			VCSCommit head=null;
			boolean branchSelected=false,devSelected=false;
			if(whereClauseParameter.equals("branch") && !(whereClauseValue.equals("all")))
			{
				try 
				{
					head=obj.getCommitTreeFromHead(workingDir, whereClauseValue);
					branchSelected=true;
					max=1;
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					head=null;
				}
			}
			else if(whereClauseParameter.equals("developer") && !(whereClauseValue.equals("all")))
			{
				devSelected=true;
			}
			while(i<max)
			{
				try 
				{
					if(branchSelected==false)
					{
						head=obj.getCommitTreeFromHead(workingDir, branches.get(i));
					}
					System.out.println(branches.get(i)+" "+head.getObjectHash());
					que.add(head);
					while(!que.isEmpty())
					{
						head=que.remove();
						if(devSelected==false)
						{
							Date d=new Date(head.getCommitTimestamp());
							Week w=new Week(d);
							Integer temp=hm.get(w);
							if(temp==null)
							{
								temp=0;
							}
							hm.put(w, temp+1);
						}
						else
						{
							if(head.getCommitter().equals(whereClauseValue))
							{
								Date d=new Date(head.getCommitTimestamp());
								Week w=new Week(d);
								Integer temp=hm.get(w);
								if(temp==null)
								{
									temp=0;
								}
								hm.put(w, temp+1);
							}
						}
						ArrayList<VCSCommit> parents=head.getParentCommits();
						int j=0,size=parents.size();
						System.out.println("j= "+j+" size= "+size);
						while(j<size)
						{
							if(branchSelected==false)
							{
								if(parents.get(j).getBranchName().equals(branches.get(i)))
								{
									System.out.println("parent "+branches.get(i)+" "+parents.get(j).getObjectHash());
									que.add(parents.get(j));
								}
							}
							else
							{
								if(parents.get(j).getBranchName().equals(whereClauseValue))
								{
									System.out.println("parent "+branches.get(i)+" "+parents.get(j).getObjectHash());
									que.add(parents.get(j));
								}
							}
							j++;
						}
					}
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
			BarChart chart=new BarChart(xAxisParameter +" vs " +yAxisParameter,yAxisParameter, xAxisParameter );
			System.out.println("keyset length " +hm.get(hm.keySet().toArray(new Week[5])[0]));
			Set<Week> set=hm.keySet();
			Iterator<Week> it=set.iterator();
			Week week;
			while(it.hasNext())
			{
				week=it.next();
				System.out.println("iterator" +week.getWeek()+" "+week.getYear());
				chart.addColName(week.getWeek()+" "+week.getYear());
			}
			chart.addToDataSet( hm.values().toArray(), "temp");
			jfChart=chart.createChart();
			
		}
		else if(xAxisParameter.equals("no of commits") && yAxisParameter.equals("developer"))              //=============================================================
		{
			//pieChart
			ArrayList<String> branches=new ArrayList<String>();
			branches=getBranches();
			int i=0,max=branches.size();
			HashMap<String,Integer> hm=new HashMap<String, Integer>();
			Queue<VCSCommit> que=new LinkedList<VCSCommit>();
			VCSCommit head=null;
			boolean branchSelected=false,devSelected=false;
			if(whereClauseParameter.equals("branch") && !(whereClauseValue.equals("all")))
			{
				try 
				{
					head=obj.getCommitTreeFromHead(workingDir, whereClauseValue);
					branchSelected=true;
					max=1;
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					head=null;
				}
			}
			else if(whereClauseParameter.equals("developer") && !(whereClauseValue.equals("all")))
			{
				devSelected=true;
			}
			while(i<max)
			{
				try 
				{
					if(branchSelected==false)
					{
						head=obj.getCommitTreeFromHead(workingDir, branches.get(i));
					}
					System.out.println(branches.get(i)+" "+head.getObjectHash());
					que.add(head);
					while(!que.isEmpty())
					{
						head=que.remove();
						if(devSelected==false)
						{
							Integer temp=hm.get(head.getCommitter());
							if(temp==null)
							{
								temp=0;
							}
							hm.put(head.getCommitter(), temp+1);
						}
						else
						{
							if(head.getCommitter().equals(whereClauseValue))
							{
								
								Integer temp=hm.get(whereClauseValue);
								if(temp==null)
								{
									temp=0;
								}
								hm.put(whereClauseValue, temp+1);
							}
						}
						ArrayList<VCSCommit> parents=head.getParentCommits();
						int j=0,size=parents.size();
						System.out.println("j= "+j+" size= "+size);
						while(j<size)
						{
							if(branchSelected==false)
							{
								if(parents.get(j).getBranchName().equals(branches.get(i)))
								{
									System.out.println("parent "+branches.get(i)+" "+parents.get(j).getObjectHash());
									que.add(parents.get(j));
								}
							}
							else
							{
								if(parents.get(j).getBranchName().equals(whereClauseValue))
								{
									System.out.println("parent "+branches.get(i)+" "+parents.get(j).getObjectHash());
									que.add(parents.get(j));
								}
							}
							j++;
						}
					}
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
			PieChart chart=new PieChart(xAxisParameter +" vs " +yAxisParameter);
			Set<String> set=hm.keySet();
			Iterator<String> it=set.iterator();
			String developer;
			while(it.hasNext())
			{
				developer=it.next();
				System.out.println("iterator " +developer);
				chart.addColName(developer);
			}
			chart.addToDataSet( hm.values().toArray(), "temp");
			jfChart=chart.createChart();
		}
		else if(xAxisParameter.equals("no of commits") && yAxisParameter.equals("branch"))              //=============================================================
		{
			//pieChart
			ArrayList<String> branches=new ArrayList<String>();
			branches=getBranches();
			int i=0,max=branches.size();
			HashMap<String,Integer> hm=new HashMap<String, Integer>();
			Queue<VCSCommit> que=new LinkedList<VCSCommit>();
			VCSCommit head=null;
			boolean branchSelected=false,devSelected=false;
			if(whereClauseParameter.equals("branch") && !(whereClauseValue.equals("all")))
			{
				try 
				{
					head=obj.getCommitTreeFromHead(workingDir, whereClauseValue);
					System.out.println("head is set and branchSelected=true  "+whereClauseValue +" head = "+head.getBranchName());
					branchSelected=true;
					max=1;
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					head=null;
				}
			}
			else if(whereClauseParameter.equals("developer") && !(whereClauseValue.equals("all")))
			{
				devSelected=true;
			}
			while(i<max)
			{
				try 
				{
					if(branchSelected==false)
					{
						head=obj.getCommitTreeFromHead(workingDir, branches.get(i));
					}
					System.out.println(head.getBranchName()+" "+head.getObjectHash());
					que.add(head);
					while(!que.isEmpty())
					{
						head=que.remove();
						if(devSelected==false)
						{
							Integer temp=hm.get(head.getBranchName());
							if(temp==null)
							{
								temp=0;
							}
							System.out.println("branch name "+head.getBranchName() +" value " +(temp+1));
							hm.put(head.getBranchName(), temp+1);
						}
						else
						{
							if(head.getCommitter().equals(whereClauseValue))
							{
								
								Integer temp=hm.get(whereClauseValue);
								if(temp==null)
								{
									temp=0;
								}
								hm.put(whereClauseValue, temp+1);
							}
						}
						ArrayList<VCSCommit> parents=head.getParentCommits();
						int j=0,size=parents.size();
						System.out.println("j= "+j+" size= "+size);
						while(j<size)
						{
							if(branchSelected==false)
							{
								if(parents.get(j).getBranchName().equals(branches.get(i)))
								{
									System.out.println("parent "+branches.get(i)+" "+parents.get(j).getObjectHash());
									que.add(parents.get(j));
								}
							}
							else
							{
								if(parents.get(j).getBranchName().equals(whereClauseValue))
								{
									System.out.println("parent "+branches.get(i)+" "+parents.get(j).getObjectHash());
									que.add(parents.get(j));
								}
							}
							j++;
						}
					}
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
			BarChart chart=new BarChart(xAxisParameter +" vs " +yAxisParameter,yAxisParameter,xAxisParameter);
			Set<String> set=hm.keySet();
			Iterator<String> it=set.iterator();
			String developer;
			while(it.hasNext())
			{
				developer=it.next();
				System.out.println("iterator " +developer);
				chart.addColName(developer);
			}
			chart.addToDataSet( hm.values().toArray(), "temp");
			jfChart=chart.createChart();
		}
		else if(xAxisParameter.equals("no of lines added & deleted") && yAxisParameter.equals("week"))              //=============================================================
		{
			ArrayList<String> branches=new ArrayList<String>();
			branches=getBranches();
			int i=0,max=branches.size();
			HashMap<Week, Integer> hmInserted=new HashMap<Week, Integer>();
			HashMap<Week, Integer> hmDeleted=new HashMap<Week, Integer>();
			Queue<VCSCommit> que=new LinkedList<VCSCommit>();
			VCSCommit head=null;
			boolean branchSelected=false,devSelected=false;
			if(whereClauseParameter.equals("branch") && !(whereClauseValue.equals("all")))
			{
				try 
				{
					head=obj.getCommitTreeFromHead(workingDir, whereClauseValue);
					branchSelected=true;
					max=1;
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					head=null;
				}
			}
			else if(whereClauseParameter.equals("developer") && !(whereClauseValue.equals("all")))
			{
				devSelected=true;
			}
			while(i<max)
			{
				try 
				{
					if(branchSelected==false)
					{
						head=obj.getCommitTreeFromHead(workingDir, branches.get(i));
					}
					System.out.println(branches.get(i)+" "+head.getObjectHash());
					que.add(head);
					while(!que.isEmpty())
					{
						head=que.remove();
						if(devSelected==false)
						{
							Date d=new Date(head.getCommitTimestamp());
							Week w=new Week(d);
							Integer tempIns=hmInserted.get(w);
							Integer tempDel=hmDeleted.get(w);
							if(tempIns==null)
							{
								tempIns=0;
							}
							if(tempDel==null)
							{
								tempDel=0;
							}
							hmInserted.put(w, tempIns+head.getNoOfLinesInserted());
							hmDeleted.put(w, tempDel+head.getNoOfLinesDeleted());
						}
						else
						{
							if(head.getCommitter().equals(whereClauseValue))
							{
								Date d=new Date(head.getCommitTimestamp());
								Week w=new Week(d);
								Integer tempIns=hmInserted.get(w);
								Integer tempDel=hmDeleted.get(w);
								if(tempIns==null)
								{
									tempIns=0;
								}
								if(tempDel==null)
								{
									tempDel=0;
								}
								hmInserted.put(w, tempIns+head.getNoOfLinesInserted());
								hmDeleted.put(w, tempDel+head.getNoOfLinesDeleted());
							}
						}
						ArrayList<VCSCommit> parents=head.getParentCommits();
						int j=0,size=parents.size();
						System.out.println("j= "+j+" size= "+size);
						while(j<size)
						{
							if(branchSelected==false)
							{
								if(parents.get(j).getBranchName().equals(branches.get(i)))
								{
									System.out.println("parent "+branches.get(i)+" "+parents.get(j).getObjectHash());
									que.add(parents.get(j));
								}
							}
							else
							{
								if(parents.get(j).getBranchName().equals(whereClauseValue))
								{
									System.out.println("parent "+branches.get(i)+" "+parents.get(j).getObjectHash());
									que.add(parents.get(j));
								}
							}
							j++;
						}
					}
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
			BarChart chart=new BarChart(xAxisParameter +" vs " +yAxisParameter,yAxisParameter, xAxisParameter );
			System.out.println("keyset length " +hmInserted.get(hmInserted.keySet().toArray(new Week[5])[0]));
			Set<Week> set=hmInserted.keySet();
			Iterator<Week> it=set.iterator();
			Week week;
			while(it.hasNext())
			{
				week=it.next();
				System.out.println("iterator" +week.getWeek()+" "+week.getYear());
				chart.addColName(week.getWeek()+" "+week.getYear());
			}
			chart.addToDataSet( hmInserted.values().toArray(), "lines inserted");
			chart.addToDataSet(hmDeleted.values().toArray(), "lines deleted");
			jfChart=chart.createChart();
		}
		else if(xAxisParameter.equals("no of lines added & deleted") && yAxisParameter.equals("branch"))              //=============================================================
		{
			ArrayList<String> branches=new ArrayList<String>();
			branches=getBranches();
			int i=0,max=branches.size();
			HashMap<String,Integer> hmInserted=new HashMap<String, Integer>();
			HashMap<String,Integer> hmDeleted=new HashMap<String, Integer>();
			Queue<VCSCommit> que=new LinkedList<VCSCommit>();
			VCSCommit head=null;
			boolean branchSelected=false,devSelected=false;
			if(whereClauseParameter.equals("branch") && !(whereClauseValue.equals("all")))
			{
				try 
				{
					head=obj.getCommitTreeFromHead(workingDir, whereClauseValue);
					System.out.println("head is set and branchSelected=true  "+whereClauseValue +" head = "+head.getBranchName());
					branchSelected=true;
					max=1;
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					head=null;
				}
			}
			else if(whereClauseParameter.equals("developer") && !(whereClauseValue.equals("all")))
			{
				devSelected=true;
			}
			while(i<max)
			{
				try 
				{
					if(branchSelected==false)
					{
						head=obj.getCommitTreeFromHead(workingDir, branches.get(i));
					}
					System.out.println(head.getBranchName()+" "+head.getObjectHash());
					que.add(head);
					while(!que.isEmpty())
					{
						head=que.remove();
						if(devSelected==false)
						{
							Integer tempIns=hmInserted.get(head.getBranchName());
							Integer tempDel=hmDeleted.get(head.getBranchName());
							if(tempIns==null)
							{
								tempIns=0;
							}
							if(tempDel==null)
							{
								tempDel=0;
							}
							System.out.println("branch name "+head.getBranchName() +" value " +(tempIns+head.getNoOfLinesInserted()));
							hmInserted.put(head.getBranchName(), tempIns+head.getNoOfLinesInserted());
							hmDeleted.put(head.getBranchName(), tempDel+head.getNoOfLinesDeleted());
						}
						else
						{
							if(head.getCommitter().equals(whereClauseValue))
							{
								
								Integer tempIns=hmInserted.get(whereClauseValue);
								Integer tempDel=hmDeleted.get(whereClauseValue);
								if(tempIns==null)
								{
									tempIns=0;
								}
								if(tempDel==null)
								{
									tempDel=0;
								}
								hmInserted.put(head.getBranchName(), tempIns+head.getNoOfLinesInserted());
								hmDeleted.put(head.getBranchName(), tempDel+head.getNoOfLinesDeleted());
							}
						}
						ArrayList<VCSCommit> parents=head.getParentCommits();
						int j=0,size=parents.size();
						System.out.println("j= "+j+" size= "+size);
						while(j<size)
						{
							if(branchSelected==false)
							{
								if(parents.get(j).getBranchName().equals(branches.get(i)))
								{
									System.out.println("parent "+branches.get(i)+" "+parents.get(j).getObjectHash());
									que.add(parents.get(j));
								}
							}
							else
							{
								if(parents.get(j).getBranchName().equals(whereClauseValue))
								{
									System.out.println("parent "+branches.get(i)+" "+parents.get(j).getObjectHash());
									que.add(parents.get(j));
								}
							}
							j++;
						}
					}
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
			BarChart chart=new BarChart(xAxisParameter +" vs " +yAxisParameter,yAxisParameter,xAxisParameter);
			Set<String> set=hmInserted.keySet();
			Iterator<String> it=set.iterator();
			String branch;
			while(it.hasNext())
			{
				branch=it.next();
				System.out.println("iterator " +branch);
				chart.addColName(branch);
			}
			chart.addToDataSet( hmInserted.values().toArray(), "lines inserted");
			chart.addToDataSet( hmDeleted.values().toArray(), "lines deleted");
			jfChart=chart.createChart();
		}
		else if(xAxisParameter.equals("no of lines added & deleted") && yAxisParameter.equals("developer"))              //=============================================================
		{
			ArrayList<String> branches=new ArrayList<String>();
			branches=getBranches();
			int i=0,max=branches.size();
			HashMap<String,Integer> hmInserted=new HashMap<String, Integer>();
			HashMap<String,Integer> hmDeleted=new HashMap<String, Integer>();
			Queue<VCSCommit> que=new LinkedList<VCSCommit>();
			VCSCommit head=null;
			boolean branchSelected=false,devSelected=false;
			if(whereClauseParameter.equals("branch") && !(whereClauseValue.equals("all")))
			{
				try 
				{
					head=obj.getCommitTreeFromHead(workingDir, whereClauseValue);
					branchSelected=true;
					max=1;
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					head=null;
				}
			}
			else if(whereClauseParameter.equals("developer") && !(whereClauseValue.equals("all")))
			{
				devSelected=true;
			}
			while(i<max)
			{
				try 
				{
					if(branchSelected==false)
					{
						head=obj.getCommitTreeFromHead(workingDir, branches.get(i));
					}
					System.out.println(branches.get(i)+" "+head.getObjectHash());
					que.add(head);
					while(!que.isEmpty())
					{
						head=que.remove();
						if(devSelected==false)
						{
							Integer tempIns=hmInserted.get(head.getCommitter());
							Integer tempDel=hmDeleted.get(head.getCommitter());
							if(tempIns==null)
							{
								tempIns=0;
							}
							if(tempDel==null)
							{
								tempDel=0;
							}
							hmInserted.put(head.getCommitter(), tempIns+head.getNoOfLinesInserted());
							hmDeleted.put(head.getCommitter(), tempDel+head.getNoOfLinesDeleted());
						}
						else
						{
							if(head.getCommitter().equals(whereClauseValue))
							{
								
								Integer tempIns=hmInserted.get(whereClauseValue);
								Integer tempDel=hmDeleted.get(whereClauseValue);
								if(tempIns==null)
								{
									tempIns=0;
								}
								if(tempDel==null)
								{
									tempDel=0;
								}
								hmInserted.put(whereClauseValue, tempIns+head.getNoOfLinesInserted());
								hmDeleted.put(whereClauseValue, tempDel+head.getNoOfLinesDeleted());
							}
						}
						ArrayList<VCSCommit> parents=head.getParentCommits();
						int j=0,size=parents.size();
						System.out.println("j= "+j+" size= "+size);
						while(j<size)
						{
							if(branchSelected==false)
							{
								if(parents.get(j).getBranchName().equals(branches.get(i)))
								{
									System.out.println("parent "+branches.get(i)+" "+parents.get(j).getObjectHash());
									que.add(parents.get(j));
								}
							}
							else
							{
								if(parents.get(j).getBranchName().equals(whereClauseValue))
								{
									System.out.println("parent "+branches.get(i)+" "+parents.get(j).getObjectHash());
									que.add(parents.get(j));
								}
							}
							j++;
						}
					}
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
			PieChart chart=new PieChart(xAxisParameter +" vs " +yAxisParameter);
			extraChart=new PieChart(xAxisParameter +" vs " +yAxisParameter);
			Set<String> set=hmInserted.keySet();
			Iterator<String> it=set.iterator();
			String developer;
			while(it.hasNext())
			{
				developer=it.next();
				System.out.println("iterator " +developer);
				chart.addColName(developer);
				extraChart.addColName(developer);
			}
			chart.addToDataSet( hmInserted.values().toArray(), "inserted");
			extraChart.addToDataSet( hmDeleted.values().toArray(), "deleted");
			jfChart=chart.createChart();
			extraJFChhart=extraChart.createChart();
		}
		else if(xAxisParameter.equals("no of developers") && yAxisParameter.equals("branch"))              //=============================================================
		{
			ArrayList<String> branches=new ArrayList<String>();
			branches=getBranches();
			int i=0,max=branches.size();
			HashMap<String,Integer> hm=new HashMap<String, Integer>();
			Queue<VCSCommit> que=new LinkedList<VCSCommit>();
			VCSCommit head=null;
			boolean branchSelected=false,devSelected=false;
			if(whereClauseParameter.equals("branch") && !(whereClauseValue.equals("all")))
			{
				try 
				{
					head=obj.getCommitTreeFromHead(workingDir, whereClauseValue);
					System.out.println("head is set and branchSelected=true  "+whereClauseValue +" head = "+head.getBranchName());
					branchSelected=true;
					max=1;
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					head=null;
				}
			}
			else if(whereClauseParameter.equals("developer") && !(whereClauseValue.equals("all")))
			{
				devSelected=true;
			}
			while(i<max)
			{
				try 
				{
					if(branchSelected==false)
					{
						head=obj.getCommitTreeFromHead(workingDir, branches.get(i));
					}
					System.out.println(head.getBranchName()+" "+head.getObjectHash());
					que.add(head);
					while(!que.isEmpty())
					{
						head=que.remove();
						if(devSelected==false)
						{
							Integer temp=hm.get(head.getBranchName());
							if(temp==null)
							{
								temp=0;
							}
							System.out.println("branch name "+head.getBranchName() +" value " +(temp+1));
							hm.put(head.getBranchName(), temp+1);
						}
						else
						{
							if(head.getCommitter().equals(whereClauseValue))
							{
								
								Integer temp=hm.get(whereClauseValue);
								if(temp==null)
								{
									temp=0;
								}
								hm.put(whereClauseValue, temp+1);
							}
						}
						ArrayList<VCSCommit> parents=head.getParentCommits();
						int j=0,size=parents.size();
						System.out.println("j= "+j+" size= "+size);
						while(j<size)
						{
							if(branchSelected==false)
							{
								if(parents.get(j).getBranchName().equals(branches.get(i)))
								{
									System.out.println("parent "+branches.get(i)+" "+parents.get(j).getObjectHash());
									que.add(parents.get(j));
								}
							}
							else
							{
								if(parents.get(j).getBranchName().equals(whereClauseValue))
								{
									System.out.println("parent "+branches.get(i)+" "+parents.get(j).getObjectHash());
									que.add(parents.get(j));
								}
							}
							j++;
						}
					}
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
			BarChart chart=new BarChart(xAxisParameter +" vs " +yAxisParameter,yAxisParameter,xAxisParameter);
			Set<String> set=hm.keySet();
			Iterator<String> it=set.iterator();
			String developer;
			while(it.hasNext())
			{
				developer=it.next();
				System.out.println("iterator " +developer);
				chart.addColName(developer);
			}
			chart.addToDataSet( hm.values().toArray(), "temp");
			jfChart=chart.createChart();
		}
		//get chart n set
		if(jfChart!=null)
		{
			ChartPanel cp=new ChartPanel(jfChart);
			panel.add(cp);
			if(extraJFChhart!=null)
			{
				ChartPanel temp=new ChartPanel(extraJFChhart);
				panel.add(temp);
			}
		}
		
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

	@SuppressWarnings("unused")
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
