package analytics;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AnalyticsEntryPoint{

	
	private JFrame mainFrame;
	
	private JLabel selectLabel;
	private JLabel versusLabel;
	
	private JPanel controlPanel;
	
	private JLabel msglabel;
	
	private JComboBox<String> xAxisComboBox;
	private JComboBox<String> yAxisComboBox;
	
	
	public AnalyticsEntryPoint()
	{
		prepareGUI();
		
	}
	
	private void prepareGUI(){
	      mainFrame = new JFrame("VCS Analytics");
	      mainFrame.setSize(400,400);
	      mainFrame.setLayout(new GridLayout(3, 1));

	      selectLabel = new JLabel("select",JLabel.CENTER );
	      versusLabel = new JLabel("vs",JLabel.CENTER);        

	      mainFrame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
		        System.exit(0);
	         }        
	      });
	      
	      xAxisComboBox= new JComboBox<String>();
	      xAxisComboBox.addItem("no of commits");
	      xAxisComboBox.addItem("avg no of commits");
	      xAxisComboBox.addItem("no of lines added & deleted");
	      xAxisComboBox.addItem("no of developers");
	      
	      controlPanel = new JPanel();
	      controlPanel.setLayout(new FlowLayout());
	      
	      msglabel=new JLabel("temp");
	      
	      controlPanel.add(xAxisComboBox);
	      controlPanel.add(versusLabel);
	      
	      mainFrame.add(selectLabel);
	      mainFrame.add(controlPanel);
	      
	      mainFrame.setVisible(true);  
	   }

}
