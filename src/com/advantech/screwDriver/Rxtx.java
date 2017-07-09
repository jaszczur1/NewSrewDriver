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
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.BufferedInputStream;

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

public class Rxtx {

    // main varible
    InputStream in;

    List getPortList() {

        List l = new ArrayList();
        Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();

        while (portIdentifiers.hasMoreElements()) {
            CommPortIdentifier pid = (CommPortIdentifier) portIdentifiers.nextElement();

            l.add(pid.getName());
        }
        return l;
    }

    void connect(String st) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException {

        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(st);
        CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
        SerialPort serialPort = (SerialPort) commPort;
        serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        // do czytania z steamu
        in = serialPort.getInputStream();

    }

    InputStream get_inputStream() {
        return in;
    }

//        public static void main(String[] args) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException {
//
//            Rxtx rxtx = new Rxtx();
//            rxtx.connect("COM3");
//            BufferedInputStream bufferedInputStream = new BufferedInputStream(rxtx.get_inputStream());
//            InputStream in  = rxtx.get_inputStream();
//
//            byte[] buffer = new byte[1024];
//            int len = -1;
//            while ((len = in.read(buffer)) > -1) {
//
//                        System.err.println(new String(buffer)+" "+len);
//
//            }
//        }
}
