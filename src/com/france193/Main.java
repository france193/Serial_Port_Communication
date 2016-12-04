package com.france193;

/**
 * Created by FLDeviOS on 22/10/2016.
 */
public class Main implements ATCommand {

    //BAUDRATE
    private static final int BAUDRATE = 9600;

    //SERIAL PORT NAME BASED ON OS
    private static final String PORT_MAC = "cu.usbserial-AI02KIFG";
    private static final String PORT_RASPBERRY = "ttyUSB0";

    public static void main(String[] args) {

        SerialRxTx serial;

        if (args.length != 0) {
            serial = new SerialRxTx(BAUDRATE, args[0].toString());
        } else {
            serial = new SerialRxTx(BAUDRATE, PORT_MAC);
        }

        serial.serialTX("Ciao, io sono francesco\nCiao, io sono francesco\n");
        serial.serialTX("Ciao, io sono francesco\n");
        //serial.close();
    }
}