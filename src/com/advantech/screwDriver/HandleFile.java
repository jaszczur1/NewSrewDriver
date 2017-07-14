/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.screwDriver;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jaszczur
 */
public class HandleFile {

    File f;
    FileWriter fw;

    void crateFile(String name) throws IOException {
        f = new File(name+".txt");
        if (!f.exists()) {
            f.createNewFile();

        } 
        fw = new FileWriter(f, true);
    }

    void set_patern(double[] patern_file) throws IOException {

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        for (int i = 0; i < patern_file.length; i++) {
            String d = String.valueOf(patern_file[i] / 3);
            fw.append((i + 1) + " " + d + " " + dateFormat.format(date));
            fw.append("\n");
        }
        fw.close();
    }

    void set_test(List<Driver> drivers) throws IOException {

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        fw.append("\n\n");
        for (int i = 0; i < drivers.size(); i++) {
            String gear = String.valueOf(drivers.get(i).gear);
            String value = String.valueOf(drivers.get(i).value);
            fw.append(gear + " " + value + " " + dateFormat.format(date));
            fw.append("\n");
        }
        fw.close();
    }
    void set_test_unEstabiltyContodb (Driver driver) throws IOException{	
    	
    //	System.err.println("zapis w locie");    	
    	Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    //	System.out.println(driver.gear+" :"+driver.value);
    	fw.append(String.valueOf(driver.gear)+" :" +driver.value+" :"+ dateFormat.format(date));
    	fw.append("\n");
    }
    void close_file() throws IOException {
    	fw.close();
    }
    
    public HandleFile() throws IOException {

    }

}
