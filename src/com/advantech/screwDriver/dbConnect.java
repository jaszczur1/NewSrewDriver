package com.advantech.screwDriver;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JFrame;

public interface dbConnect {

	boolean connect(JFrame frame) throws SQLException;
	void disconnect() throws SQLException;
	List<Driver> get_patern(String name) throws SQLException;

}
