package analytics;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import vcs.Constants;

public class AnalyticsEntryPoint{

	
	private JFrame mainFrame;
	
	private JLabel selectLabel;
	private JLabel versusLabel;
	
	private JPanel controlPanel;
	
	private JLabel whereLabel;
	private JLabel equalsLabel;
	private JTabbedPane tabbedPane;
	
	private JComboBox<String> xAxisComboBox;
	private JComboBox<String> yAxisComboBox;
	private JComboBox<String> extraParametersComboBox;
	private JComboBox<String> extraValuesComboBox;
	
	private JButton okButton;

	private String workingDir;
	
	public AnalyticsEntryPoint(String workingDir)
	{
		this.workingDir=workingDir;
		prepareGUI();
	}
	
	private void prepareGUI(){
	      mainFrame = new JFrame("VCS Analytics");
	      mainFrame.setSize(600,400);
	      mainFrame.setLayout(new GridLayout(1, 1));

	      selectLabel = new JLabel("select",JLabel.CENTER );
	      versusLabel = new JLabel("vs",JLabel.CENTER);        

	      mainFrame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
		        System.exit(0);
	         }        
	      });
	      
	      tabbedPane =new JTabbedPane();
	      
	      xAxisComboBox= new JComboBox<String>();
	      xAxisComboBox.addItem("no of commits");
	      xAxisComboBox.addItem("no of lines added & deleted");
	      xAxisComboBox.addItem("no of developers");
	      xAxisComboBox.setSize(new Dimension(100,50));
	      
	      
	      yAxisComboBox= new JComboBox<String>();
	      yAxisComboBox.addItem("week");
	      yAxisComboBox.addItem("branch");
	      yAxisComboBox.addItem("developer");
	      yAxisComboBox.addItem("commit");
	      
	      
	      extraParametersComboBox=new JComboBox<String>();
	      extraParametersComboBox.addItem("branch");
	      extraParametersComboBox.addItem("developer");
	      
	      controlPanel = new JPanel();
	      controlPanel.setLayout(new FlowLayout());
	      
	      whereLabel=new JLabel("where");
	      equalsLabel=new JLabel("=");
	      
	      
	      
	      extraValuesComboBox=new JComboBox<String>();
	      extraValuesComboBox.removeAllItems();
	      extraValuesComboBox.addItem("all");
	      ArrayList<String> branches =new ArrayList<String>();
		  branches =getBranches();
		  int i=0,max=branches.size();
		  while(i<max)
		  {
			  extraValuesComboBox.addItem(branches.get(i));
			  i++;
		  }
    	  
	      extraParametersComboBox.addItemListener(new ItemListener() 
	      {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(extraParametersComboBox.getSelectedItem().equals("branch"))
			      {
					  extraValuesComboBox.removeAllItems();
					  extraValuesComboBox.addItem("all");
					  ArrayList<String> branches =new ArrayList<String>();
					  branches =getBranches();
					  int i=0,max=branches.size();
					  while(i<max)
					  {
						  extraValuesComboBox.addItem(branches.get(i));
						  i++;
					  }
			      }
			      else if(extraParametersComboBox.getSelectedItem().equals("developer"))
			      {
			    	  extraValuesComboBox.removeAllItems();
			    	  ArrayList<String> developers=new ArrayList<String>();
			    	  developers=getDevelopers();
			    	  int i=0,max=developers.size();
			    	  extraValuesComboBox.addItem("all");
			    	  while(i<max)
			    	  {
			    		  extraValuesComboBox.addItem(developers.get(i));
			    		  i++;
			    	  }
			      }
			}
		});
	      okButton=new JButton("OK");
	      
	      controlPanel.add(selectLabel);
	      controlPanel.add(xAxisComboBox);
	      controlPanel.add(versusLabel);
	      controlPanel.add(yAxisComboBox);
	      controlPanel.add(whereLabel);
	      controlPanel.add(extraParametersComboBox);
	      controlPanel.add(equalsLabel);
	      controlPanel.add(extraValuesComboBox);
	      controlPanel.add(okButton);
	      tabbedPane.addTab("start", controlPanel);
	      
	      
	      okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(e.getSource().equals(okButton))
				{
					System.out.println(selectLabel.getText() +" " +xAxisComboBox.getSelectedItem().toString() +" " +versusLabel.getText() +" " +yAxisComboBox.getSelectedItem().toString() +" "+whereLabel.getText() +" "+extraParametersComboBox.getSelectedItem().toString() +" = "+extraValuesComboBox.getSelectedItem().toString());
					//String query=selectLabel.getText() +" " +xAxisComboBox.getSelectedItem().toString() +" " +versusLabel.getText() +" " +yAxisComboBox.getSelectedItem().toString();
					if(!xAxisComboBox.getSelectedItem().toString().equals(yAxisComboBox.getSelectedItem().toString()))
					{
						ChartTab obj= new ChartTab(xAxisComboBox.getSelectedItem().toString(),yAxisComboBox.getSelectedItem().toString(),extraParametersComboBox.getSelectedItem().toString(),extraValuesComboBox.getSelectedItem().toString(),workingDir);
						JPanel tab=obj.getChartPanel();
						tabbedPane.addTab("chartTab", tab);
					}
				}
			}
		});
	      
	      	      
	      
	      
	      mainFrame.add(tabbedPane);
	      mainFrame.setVisible(true);
	   }
	
	protected ArrayList<String> getBranches() {
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
