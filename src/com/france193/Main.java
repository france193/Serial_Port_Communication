package com.france193;

import gnu.io.*;

public class Main implements ATCommand {

    //TIMEOUT
    private static final int TIME_OUT = 2000;

    //BAUDRATE
    private static final int BAUDRATE = 9600;

    //SERIAL PORT NAME
    private static final String PORT = "/dev/cu.usbserial-AI02KIFG";

    public static void main(String[] args) {

        SerialRxTx serial = new SerialRxTx(TIME_OUT, BAUDRATE, PORT);
        serial.listPorts();
        System.out.println("\nConnecting to " + PORT);
        serial.initialize();
        //serial.write(at_hwgetdietemp);
    }

}


