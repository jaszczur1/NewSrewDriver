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
public class Driver {

    int gear;
    double value;
    String name;

    public Driver(int gear, double value) {
        this.gear = gear;
        this.value = value;
    }
    void setName(String name) {
    	this.name = name;
    };
    
    
}
