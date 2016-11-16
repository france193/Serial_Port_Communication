package com.france193;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

//import library.rxtx.*;
import gnu.io.*;

/**
 * Created by FLDeviOS on 22/10/2016.
 */
public class SerialRxTx implements SerialPortEventListener {

    /*
    Connection conn = null;
    String url = "jdbc:mysql://2.238.140.10:3306/";
    String dbName = "Geologging";
    String driver = "com.mysql.jdbc.Driver";
    String userName = "root";
    String password = "Thisistherootpassword";
    */

    private static boolean GPS = true;
    private static int timeout;
    private static int baudrate;
    private static String port;
    private static Enumeration portList;
    private static SerialPort serialPort;
    private static BufferedReader input;
    private static OutputStream output;

    private int counter = 0;

    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    public SerialRxTx(int timeout, int baudrate, String port) {
        this.timeout = timeout;
        this.baudrate = baudrate;
        this.port = port;
    }

    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                if (input.ready()) {
                    String line = input.readLine();

                    if (GPS) {

                        //for GPS module
                        if (line.contains("$GPGGA")) {
                            printGeologgingData(line);
                        }
                    } else {

                        System.out.println(line);
                    }
                }

            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    private void printGeologgingData(String line) {
        //split all string separated by ,
        List<String> infoList = Arrays.asList(line.split(","));

        counter++;

        //DateTime read from java
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
        Date date = new Date();

        //DateTime read from GPS module
        String time = infoList.get(1).toString();
        int h, m, s, z;
        h = Integer.parseInt(time.substring(0, 2));
        //italy time-zone is z=+1
        h++;
        m = Integer.parseInt(time.substring(2, 4));
        s = Integer.parseInt(time.substring(4, 6));
        z = Integer.parseInt(time.substring(7, 10));

        //read coordinates
        //TODO N and E are +, S and W are -
        int latitude_deg, longitude_deg;
        float latitude_minutes, longitude_minutes;
        String latitude, longitude;
        latitude_deg = Integer.parseInt(infoList.get(2).toString().substring(0, 2));
        latitude_minutes = Float.parseFloat(infoList.get(2).toString().substring(2));
        longitude_deg = Integer.parseInt(infoList.get(4).toString().substring(0, 3));
        longitude_minutes = Float.parseFloat(infoList.get(4).toString().substring(3));
        latitude = "+" + latitude_deg + "° " + latitude_minutes;
        longitude = "+" + longitude_deg + "° " + longitude_minutes;

        //create query
        String query = "INSERT INTO GEOLOCATION_DATA(ID,TIME,FIX,SATELLITES,LATITUDE,LONGITUDE,ALTITUDE)" +
                "VALUES ('" + counter +
                "','" + dateFormat.format(date).toString() +
                "','" + infoList.get(6).toString() +
                "','" + infoList.get(7).toString() +
                "','" + latitude.toString() +
                "\\'','" + longitude.toString() +
                "\\'','" + infoList.get(9).toString() +"');";

        try {
            String filename = "Geolocation_logging.txt";
            FileWriter fw = new FileWriter(filename, true); //the true will append the new data
            fw.write(query + "\n");//appends the string to the file
            fw.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }

        /*
        //saving into databse
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(SerialRxTx.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        */

        //debug printing
        System.out.println("-----> GEOLOCATION REPORT #" + counter + "<-----\n" +
                //" * [time (hh:mm:ss)] - "+String.format("%02d",h)+":"+String.format("%02d",m)+":"+String.format("%02d",s)+"\n"+
                " * [time (hh:mm:ss)] * " + dateFormat.format(date) + "\n" +
                " * [latitude (N)]    * " + latitude + "\n" +
                " * [longitude (E)]   * " + longitude + "\n" +
                " * [fix (ok if >0)]  * " + infoList.get(6).toString() + "\n" +
                " * [satellites (#)]  * " + infoList.get(7).toString() + "\n" +
                " * [altitude (m)]    * " + infoList.get(9).toString() + "\n");
    }

    public void initialize() {
        CommPortIdentifier portId = null;

        portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals(port)) {
                    break;
                }
            }
        }

        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }

        try {
            serialPort = (SerialPort) portId.open(this.getClass().getName(), timeout);
            serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            //set event listener
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public void write(String message) {
        System.out.println("-> TX: " + message);
        try {
            output.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listPorts() {
        Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();

        System.out.println("\nAvailable serial ports:");
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            System.out.println(" > " + portIdentifier.getName() + " - " + getPortTypeName(portIdentifier.getPortType()));
        }
    }

    public String getPortTypeName(int portType) {
        switch (portType) {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }

}