package com.france193;

/**
 * Created by FLDeviOS on 22/10/2016.
 */
public class Main implements ATCommand {

    //BAUDRATE
    private static final int BAUDRATE = 9600;

    //SERIAL PORT NAME
    private static final String PORT = "cu.usbserial-AI02KIFG";

    public static void main(String[] args) {
        SerialRxTx serial = new SerialRxTx(BAUDRATE, PORT);
    }
}