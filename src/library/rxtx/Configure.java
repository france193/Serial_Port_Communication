package library.rxtx;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.IOException;

class Configure extends Frame {
    Checkbox[] cb;
    Panel p1;
    static final int PORT_SERIAL = 1;
    static final int PORT_PARALLEL = 2;
    int PortType = 1;
    String EnumMessage = new String("gnu.io.rxtx.properties has not been detected.\n\nThere is no consistant means of detecting ports on this operating System.  It is necessary to indicate which ports are valid on this system before proper port enumeration can happen.  Please check the ports that are valid on this system and select Save");

    private void saveSpecifiedPorts() {
        String var2 = new String(System.getProperty("java.home"));
        String var3 = System.getProperty("path.separator", ":");
        String var4 = System.getProperty("file.separator", "/");
        String var5 = System.getProperty("line.separator");
        String var1;
        if (this.PortType == 1) {
            var1 = new String(var2 + var4 + "lib" + var4 + "gnu.io.rxtx.SerialPorts");
        } else {
            if (this.PortType != 2) {
                System.out.println("Bad Port Type!");
                return;
            }

            var1 = new String(var2 + "gnu.io.rxtx.ParallelPorts");
        }

        System.out.println(var1);

        try {
            FileOutputStream var7 = new FileOutputStream(var1);

            for (int var8 = 0; var8 < 128; ++var8) {
                if (this.cb[var8].getState()) {
                    String var6 = new String(this.cb[var8].getLabel() + var3);
                    var7.write(var6.getBytes());
                }
            }

            var7.write(var5.getBytes());
            var7.close();
        } catch (IOException var9) {
            System.out.println("IOException!");
        }

    }

    void addCheckBoxes(String var1) {
        int var2;
        for (var2 = 0; var2 < 128; ++var2) {
            if (this.cb[var2] != null) {
                this.p1.remove(this.cb[var2]);
            }
        }

        for (var2 = 1; var2 < 129; ++var2) {
            this.cb[var2 - 1] = new Checkbox(var1 + var2);
            this.p1.add("NORTH", this.cb[var2 - 1]);
        }

    }

    public Configure() {
        short var1 = 640;
        short var2 = 480;
        this.cb = new Checkbox[128];
        final Frame var3 = new Frame("Configure gnu.io.rxtx.properties");
        String var4 = System.getProperty("file.separator", "/");
        String var5;
        if (var4.compareTo("/") != 0) {
            var5 = "COM";
        } else {
            var5 = "/dev/";
        }

        var3.setBounds(100, 50, var1, var2);
        var3.setLayout(new BorderLayout());
        this.p1 = new Panel();
        this.p1.setLayout(new GridLayout(16, 4));
        ActionListener var6 = new ActionListener() {
            public void actionPerformed(ActionEvent var1) {
                String var2 = var1.getActionCommand();
                if (var2.equals("Save")) {
                    Configure.this.saveSpecifiedPorts();
                }

            }
        };
        this.addCheckBoxes(var5);
        TextArea var7 = new TextArea(this.EnumMessage, 5, 50, 3);
        var7.setSize(50, var1);
        var7.setEditable(false);
        Panel var8 = new Panel();
        var8.add(new Label("Port Name:"));
        TextField var9 = new TextField(var5, 8);
        var9.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent var1) {
                Configure.this.addCheckBoxes(var1.getActionCommand());
                var3.setVisible(true);
            }
        });
        var8.add(var9);
        Checkbox var10 = new Checkbox("Keep Ports");
        var8.add(var10);
        Button[] var11 = new Button[6];
        int var12 = 0;

        for (int var13 = 4; var13 < 129; ++var12) {
            var11[var12] = new Button("1-" + var13);
            var11[var12].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent var1) {
                    int var2 = Integer.parseInt(var1.getActionCommand().substring(2));

                    for (int var3x = 0; var3x < var2; ++var3x) {
                        Configure.this.cb[var3x].setState(!Configure.this.cb[var3x].getState());
                        var3.setVisible(true);
                    }

                }
            });
            var8.add(var11[var12]);
            var13 *= 2;
        }

        Button var14 = new Button("More");
        Button var15 = new Button("Save");
        var14.addActionListener(var6);
        var15.addActionListener(var6);
        var8.add(var14);
        var8.add(var15);
        var3.add("South", var8);
        var3.add("Center", this.p1);
        var3.add("North", var7);
        var3.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent var1) {
                System.exit(0);
            }
        });
        var3.setVisible(true);
    }

    public static void main(String[] var0) {
        new Configure();
    }
}
