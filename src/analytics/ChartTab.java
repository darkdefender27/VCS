package analytics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class ChartTab {

	public ChartTab()
	{
		
	}

	public JPanel getChartPanel() {
		// TODO Auto-generated method stub
		JPanel panel=new JPanel();
		GridLayout layout = new GridLayout(1, 2);
		panel.setSize(new Dimension(500, 300));
		panel.setLayout(layout);
		panel.setBackground(Color.BLACK);
		return panel;
	}
}
