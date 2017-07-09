/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.screwDriver;

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

public class DB {

    Okno okno;
    Connection con = null;

    public DB(Okno okno) {
        this.okno = okno;
    }

    boolean connect() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/screwdriver", "root", "");
        } catch (Exception e) {
            okno.main_area.append("bład polaczenia z baza \n");
            System.out.println("bład polaczenia z baza \n" + e);
        }
        // handle for bad connect
        if (con.getWarnings() == null) {
            okno.main_area.append("Połacznie z baza ustanowione! \nUstaw Rs\n");
        return  true;
        }
        return  false;
    }

    void disconnect() throws SQLException {
        con.close();
    }

    void set_patern(double[] ds, String name) throws SQLException {

        System.out.println(name + " :" + ds.length);

        Statement test = con.createStatement();
        ResultSet rs = test.executeQuery("SELECT patern.id_driver FROM patern WHERE (SELECT driver.id_driver FROM driver WHERE driver.barcode = '" + name + "')");
        //check if given driver exist rs.first() return > 1
        if (rs.first()) {
            okno.main_area.append("zmiana wartosci\n");
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

    List<Driver> get_patern(String name) throws SQLException {
        List<Driver> l = new ArrayList<Driver>();

        Statement get_patern_val = con.createStatement();
        ResultSet s = get_patern_val.executeQuery("select * from patern where patern.id_driver = (SELECT driver.id_driver FROM driver WHERE driver.barcode =  '" + name + "')");
        int[] tab = new int[5];
        tab[0] = 3;
        tab[1] = 5;
        tab[2] = 7;
        tab[3] = 9;
        tab[4] = 11;

        okno.main_area.append("\n");
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
