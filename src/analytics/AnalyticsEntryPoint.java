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
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AnalyticsEntryPoint{

	
	private JFrame mainFrame;
	
	private JLabel selectLabel;
	private JLabel versusLabel;
	
	private JPanel controlPanel;
	
	private JLabel whereLabel;
	private JLabel equalsLabel;
	
	private JComboBox<String> xAxisComboBox;
	private JComboBox<String> yAxisComboBox;
	private JComboBox<String> extraParametersComboBox;
	private JComboBox<String> extraValuesComboBox;
	
	private JButton okButton;
	
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
	      
	      controlPanel.add(xAxisComboBox);
	      controlPanel.add(versusLabel);
	      controlPanel.add(yAxisComboBox);
	      controlPanel.add(extraParametersComboBox);
	      
	      extraValuesComboBox=new JComboBox<String>();
	      extraValuesComboBox.addItem("all");
    	  extraValuesComboBox.addItem("master");
    	  extraValuesComboBox.addItem("b1");
    	  extraValuesComboBox.addItem("temp");
    	  
	      extraParametersComboBox.addItemListener(new ItemListener() 
	      {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(extraParametersComboBox.getSelectedItem().equals("branch"))
			      {
					  extraValuesComboBox.removeAllItems();
					  extraValuesComboBox.addItem("all");
			    	  extraValuesComboBox.addItem("master");
			    	  extraValuesComboBox.addItem("b1");
			    	  extraValuesComboBox.addItem("temp");
			      }
			      else if(extraParametersComboBox.getSelectedItem().equals("developer"))
			      {
			    	  extraValuesComboBox.removeAllItems();
			    	  extraValuesComboBox.addItem("all");
			    	  extraValuesComboBox.addItem("ambarish.v.rao@gmail.com");
			    	  extraValuesComboBox.addItem("rounak.nandanwar@gmail.com");
			    	  extraValuesComboBox.addItem("shubham.utwal@gmail.com");
			    	  extraValuesComboBox.addItem("prashant.aher@gmail.com");
			      }
			}
		});
	      okButton=new JButton("OK");
	      
	      mainFrame.add(selectLabel);
	      mainFrame.add(controlPanel);
	      mainFrame.add(whereLabel);
	      mainFrame.add(extraParametersComboBox);
	      mainFrame.add(equalsLabel);
	      mainFrame.add(extraValuesComboBox);
	      mainFrame.add(okButton);
	      okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(e.getSource().equals(okButton))
				{
					System.out.println(selectLabel.getText() +" " +xAxisComboBox.getSelectedItem().toString() +" " +versusLabel.getText() +" " +yAxisComboBox.getSelectedItem().toString() +" "+whereLabel.getText() +" "+extraParametersComboBox.getSelectedItem().toString() +" = "+extraValuesComboBox.getSelectedItem().toString());
					String query=selectLabel.getText() +" " +xAxisComboBox.getSelectedItem().toString() +" " +versusLabel.getText() +" " +yAxisComboBox.getSelectedItem().toString();
					if(query.equals("select no of commits vs week"))
					{
						
					}
				}
			}
		});
	      mainFrame.setVisible(true);
	   }

}
