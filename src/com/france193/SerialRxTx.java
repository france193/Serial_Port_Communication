package com.france193;

import com.fazecast.jSerialComm.*;

import java.util.*;

/**
 * Created by FLDeviOS on 22/10/2016.
 */
public class SerialRxTx {

    private HashMap<String, SerialPort> serialPorts;
    private SerialPort serialPort;

    public SerialRxTx(int baudrate, String portNAme) {

        serialPorts = new HashMap<>();

        try {
            // read all ports
            readAllPortsAvailable();

            // prints all ports
            printAllPortsAvailable();

            // retrive required port
            if ( (serialPort = findPort(portNAme)) == null) {
                System.out.println("(!) error, port not found!");
                System.exit(2);
            }

            /*
            // if it is already opened, close it
            if (serialPort.isOpen()) {
                System.out.println("> Port is already in use, closing port... ");
                serialPort.closePort();
                System.out.println("> Port correctly closed!");
            }
            */

            // open port and set baudrate required
            System.out.println("> Opening port...");
            if (serialPort.openPort()) {
                System.out.println("> Port correctly opened!");
                System.out.println("> Setting baudrate: " + baudrate + "...");
                serialPort.setBaudRate(baudrate);
                System.out.println("> Baudrate correctly setted!");
            } else {
                System.out.println("(!) error opening port!");
                System.exit(1);
            }

            System.out.println("> Setting data listener...");
            serialPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                        return;
                    }
                    byte[] newData = new byte[serialPort.bytesAvailable()];
                    int numRead = serialPort.readBytes(newData, newData.length);
                    System.out.println("Read " + numRead + " bytes.");
                }
            });
            System.out.println("> Listener correctly setted!");

            System.out.println("> Waiting for reading data from port...");

            System.out.println("> Closing port... ");
            serialPort.closePort();
            System.out.println("> Port correctly closed!");

        } catch (NullPointerException e) {
            System.out.println("(!) Error -> NullPointerException " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }

    private void readAllPortsAvailable() {
        for (SerialPort p : SerialPort.getCommPorts()) {
            serialPorts.put(p.getSystemPortName(), p);
        }
    }

    private void printAllPortsAvailable() {
        System.out.println("Availables SerialPort are: ");

        for (Map.Entry<String, SerialPort> entry : serialPorts.entrySet()) {
            String key = entry.getKey();
            SerialPort value = entry.getValue();

            System.out.println("\t > " + key + " \t- " + value.getDescriptivePortName());
        }
        System.out.println("");
    }

    private SerialPort findPort(String portName) {
        System.out.println("> Finding port: " + portName + "... ");

        if (portName == null) {
            System.out.println("(!) error, incorrect port name.");
            return null;
        }

        SerialPort s = serialPorts.get(portName.toString());

        if (s != null) {
            System.out.println("> Port found!");
            return s;
        }

        return null;
    }

    /*
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
                "\\'','" + infoList.get(9).toString() + "');";

        try {
            String filename = "Geolocation_logging.txt";
            FileWriter fw = new FileWriter(filename, true); //the true will append the new data
            fw.write(query + "\n");//appends the string to the file
            fw.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }

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

    public void write(String message) {
        System.out.println(" (TX) > writing: \"" +
                message.replace("\n","").replace("\r","") +
                "\" to Serial...");
        try {
            output.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}