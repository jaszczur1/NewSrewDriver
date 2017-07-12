package com.advantech.screwDriver;

import java.awt.BorderLayout;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PatternTable extends JFrame{

	PatternTable patternTable = this;
	DB_to_Table  to_Table = new DB_to_Table();
	public JTable table;
	
	DefaultTableModel defaultModel = new DefaultTableModel();

	public PatternTable() throws SQLException {
					
	
		//	String[] columnNames = { "id", "name", "#1", "#2", "#3", "#4", "#5", "#6","#7", "#8", "#8", "#9", "#10"};

		String[] columnNames = { "id", "name"};
		
		Object[][] data = { { "Kathy", "Smith"}};

		
//		
//		defaultModel.addRow(new Object[] { "data", "data", "data",
//        		"data", "data", "data" });
		
	//	table.setValueAt(new Object(), 0, 0);
		
		table = new JTable(defaultModel);
		
	//	table.setValueAt(new Object(), 0, 0);
		
		patternTable.setLayout(new BorderLayout());
	    patternTable.add(table);
		patternTable.add(table.getTableHeader(), BorderLayout.NORTH);
		// TODO Auto-generated constructor stub
		setVisible(true);
		patternTable.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		patternTable.setSize(800, 600);
		
		to_Table.connect(patternTable);	
		to_Table.get_patern();
	}
	
	public static void main(String aString[]) throws SQLException {
			
		new PatternTable();
  
	
	
	}
	private static final long serialVersionUID = 1L;
	
}
