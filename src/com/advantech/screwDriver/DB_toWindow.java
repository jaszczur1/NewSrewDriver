/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.screwDriver;

import java.awt.Frame;
/**
 *
 * @author jaszczur
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;


public class DB_toWindow implements dbConnect{

    Okno okno = null;
    Connection con = null;
    
//    public DB_toWindow (PatternTable patternTable) {
//		this.patternTable = patternTable;
//	}
    
    public boolean connect(JFrame frame) throws SQLException {
    	okno = (Okno) frame;
    	
    	try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/screwdriver", "root", "");
        } catch (Exception e) {
            okno.main_area.append("Blad polaczenia z baza! \n");
//            System.out.println("blad polaczenia z baza \n" + e);
            okno.main_area.append("Mozliwy test kazdego biegu do pliku \nUstaw Rs");
            return false;
        }
        // handle for not bad connected
        if (con.getWarnings() == null) {
            okno.main_area.append("Polacznie z baza ustanowione! \nUstaw Rs");
        return  true;
        }
        return  false;
    }

    public void disconnect() throws SQLException {
        con.close();
    }

    void set_patern(double[] ds, String name) throws SQLException {

        System.out.println(name + " :" + ds.length);

        Statement test = con.createStatement();
        ResultSet rs = test.executeQuery("SELECT patern.id_driver FROM patern WHERE (SELECT driver.id_driver FROM driver WHERE driver.barcode = '" + name + "')");
        //check if given driver exist rs.first() return > 1
        if (rs.first()) {
            okno.main_area.append("zmiana wartosci wzorca kalibracji\n");
            Statement update = con.createStatement();

            for (int i = 0; i < ds.length; i++) {
                double d = ds[i];

                System.err.println(d);
                int gear = i + 1;
                update.executeUpdate("UPDATE patern SET result ='" + d / 3 + "' WHERE gear= '" + gear + "' AND id_driver = (SELECT driver.id_driver FROM driver WHERE driver.barcode = '" + name + "')");
                
            }

        } else {
            okno.main_area.append("nieznana wkretarka dodana do bazy\n");
            Statement create_driver = con.createStatement();
            create_driver.execute("insert into driver (barcode) values ('" + name + "')");
            int gear = 1;
            for (int i = 0; i < ds.length; i++) {
                double d = ds[i];
                System.out.println(d / 3);
                Statement stmt = con.createStatement();
                stmt.execute("INSERT into patern (gear, result, id_driver) SELECT '" + gear + "','" + d / 3 + "',(SELECT driver.id_driver FROM driver WHERE driver.barcode = '" + name + "')");
                gear++;
            }
        }
    }

    public List<Driver> get_patern(String name) throws SQLException {
        List<Driver> l = new ArrayList<Driver>();

        Statement get_patern_val = con.createStatement();
        ResultSet s = get_patern_val.executeQuery("select * from patern where patern.id_driver = (SELECT driver.id_driver FROM driver WHERE driver.barcode =  '" + name + "')");
        int[] tab = new int[5];
        tab[0] = 3;
        tab[1] = 5;
        tab[2] = 7;
        tab[3] = 9;
        tab[4] = 11;

//        okno.main_area.append("\n");
        while (s.next()) {
            int gear = s.getInt("gear");
            double result = s.getDouble("result");

            for (int i = 0; i < tab.length; i++) {
                if (s.getInt("result") == tab[i]) {
                    okno.main_area.append(gear + " " + result + "\n");
                    l.add(new Driver(gear, result));
                }
            }
        }
        return l;
    }

    void test(List<Driver> drivers, String name) throws SQLException {

        System.out.println(drivers.size());

//        Statement test = con.createStatement();
//        ResultSet rs = test.executeQuery("SELECT test.id_driver FROM test WHERE (SELECT driver.id_driver FROM driver WHERE driver.barcode = '" + name + "')");
//        //check if given driver exist rs.first() return > 1
//        if (rs.first()) {
//
//            System.err.println("wpis już jest");
//            okno.main_area.append("zmiana wartosci\n");
//            Statement update = con.createStatement();
//            int quantity = 0;
//            while (quantity < drivers.size()) {
//                update.executeUpdate("UPDATE test SET result ='" + drivers.get(quantity).value + "' WHERE gear= '" + drivers.get(quantity).gear + "' AND id_driver = (SELECT driver.id_driver FROM driver WHERE driver.barcode = '" + name + "')");
//                quantity++;
//            }
//        } else {
        okno.main_area.append("\nWkretarka :" + name + " przetesowana");
        // add new first values to db
        for (int i = 0; i < drivers.size(); i++) {
            double d = drivers.get(i).value;
            int gear = drivers.get(i).gear;
            Statement stmt = con.createStatement();
            stmt.execute("INSERT into test (gear, result, id_driver) SELECT '" + gear + "','" + d + "',(SELECT driver.id_driver FROM driver WHERE driver.barcode = '" + name + "')");
        }
    }
}
