package com.advantech.screwDriver;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.sql.Connection;
import java.sql.ResultSet;
import javax.swing.JFrame;

public class DB_to_Table implements dbConnect {

	Connection con = null;
	PatternTable table = null;

	@Override
	public boolean connect(JFrame frame) throws SQLException {

		table = (PatternTable) frame;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/screwdriver", "root", "");
		} catch (Exception e) {

			// System.out.println("blad polaczenia z baza \n" + e);
			// okno.main_area.append("Mozliwy test kazdego biegu do pliku \nUstaw Rs");
			return false;
		}
		// handle for not bad connected
		if (con.getWarnings() == null) {
			// okno.main_area.append("Polacznie z baza ustanowione! \nUstaw Rs\n");
			return true;
		}
		return false;
	}

	@Override
	public void disconnect() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Driver> get_patern(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void get_patern() throws SQLException {


		String Query = "SELECT *, driver.barcode FROM patern, driver WHERE patern.id_driver = driver.id_driver";
		Statement get_patern_val = con.createStatement();
		ResultSet s = get_patern_val.executeQuery(Query);

		table.defaultModel.addColumn("id");
		table.defaultModel.addColumn("barcode");
		table.defaultModel.addColumn("#1");
		table.defaultModel.addColumn("#2");
		table.defaultModel.addColumn("#3");
		table.defaultModel.addColumn("#4");
		table.defaultModel.addColumn("#5");
		table.defaultModel.addColumn("#6");
		table.defaultModel.addColumn("#7");
		table.defaultModel.addColumn("#8");
		table.defaultModel.addColumn("#9");
		table.defaultModel.addColumn("#10");
		table.defaultModel.addColumn("Data");
		
		
//		for (int count = 1; count <= 10; count++) {
//	        Vector<Object> data = new Vector<Object>();
//	       
//	     //   data.add(count);
//       // data.add("Project Title" + count);
////	        data.add("Start");
////	        data.add("Stop");
////	        data.add("Pause");
////	        data.add("Status");
////	        System.out.println("test :- " + count);
//	       table.defaultModel.addRow(data);
//	       // System.err.println(table.table);
//		}	
		
		int i =0;
		int j= 2;
		Vector<Object> data = new Vector<Object>();
		table.defaultModel.addRow(data);
		
		while (s.next()) {
		//	System.err.println("get patern");
			
			table.table.setValueAt(s.getString("id_driver"), i, 0);
			table.table.setValueAt(s.getString("barcode"), i, 1);
			table.table.setValueAt(s.getDate("execution_time_patern"), i, 12);
			table.table.setValueAt(s.getDouble("result"), i, j);
			j++;
			if(j == 12) {
				j = 2;
				i++;
				Vector<Object> data1 = new Vector<Object>();
				table.defaultModel.addRow(data1);
			}
			
//			table.defaultModel.addRow(new Object[] { s.getString("id_driver"), s.getString("barcode"), s.getDouble("result")
		       		
		}
		
	};
}
