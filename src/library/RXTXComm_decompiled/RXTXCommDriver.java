package library.RXTXComm_decompiled;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

public class RXTXCommDriver implements CommDriver {
    private static final boolean debug = false;
    private static final boolean devel = true;
    private String deviceDirectory;
    private String osName;

    public RXTXCommDriver() {
    }

    private native boolean registerKnownPorts(int var1);

    private native boolean isPortPrefixValid(String var1);

    private native boolean testRead(String var1, int var2);

    private native String getDeviceDirectory();

    public static native String nativeGetVersion();

    private final String[] getValidPortPrefixes(String[] var1) {
        String[] var2 = new String[256];
        if (var1 == null) {
            ;
        }

        int var3 = 0;

        for (int var4 = 0; var4 < var1.length; ++var4) {
            if (this.isPortPrefixValid(var1[var4])) {
                var2[var3++] = new String(var1[var4]);
            }
        }

        String[] var5 = new String[var3];
        System.arraycopy(var2, 0, var5, 0, var3);
        if (var2[0] == null) {
            ;
        }

        return var5;
    }

    private void checkSolaris(String var1, int var2) {
        char[] var3 = new char[]{'['};

        for (var3[0] = 97; var3[0] < 123; ++var3[0]) {
            if (this.testRead(var1.concat(new String(var3)), var2)) {
                CommPortIdentifier.addPortName(var1.concat(new String(var3)), var2, this);
            }
        }

    }

    private void registerValidPorts(String[] var1, String[] var2, int var3) {
        boolean var4 = false;
        boolean var5 = false;
        if (var1 != null && var2 != null) {
            for (int var12 = 0; var12 < var1.length; ++var12) {
                for (int var13 = 0; var13 < var2.length; ++var13) {
                    String var6 = var2[var13];
                    int var7 = var6.length();
                    String var8 = var1[var12];
                    if (var8.length() >= var7) {
                        String var9 = var8.substring(var7).toUpperCase();
                        String var10 = var8.substring(var7).toLowerCase();
                        if (var8.regionMatches(0, var6, 0, var7) && var9.equals(var10)) {
                            String var11;
                            if (this.osName.toLowerCase().indexOf("windows") == -1) {
                                var11 = new String(this.deviceDirectory + var8);
                            } else {
                                var11 = new String(var8);
                            }

                            if (!this.osName.equals("Solaris") && !this.osName.equals("SunOS")) {
                                if (this.testRead(var11, var3)) {
                                    CommPortIdentifier.addPortName(var11, var3, this);
                                }
                            } else {
                                this.checkSolaris(var11, var3);
                            }
                        }
                    }
                }
            }
        }

    }

    public void initialize() {
        this.osName = System.getProperty("os.name");
        this.deviceDirectory = this.getDeviceDirectory();

        for (int var1 = 1; var1 <= 2; ++var1) {
            if (!this.registerSpecifiedPorts(var1) && !this.registerKnownPorts(var1)) {
                this.registerScannedPorts(var1);
            }
        }

    }

    private void addSpecifiedPorts(String var1, int var2) {
        String var3 = System.getProperty("path.separator", ":");
        StringTokenizer var4 = new StringTokenizer(var1, var3);

        while (var4.hasMoreElements()) {
            String var5 = var4.nextToken();
            if (this.testRead(var5, var2)) {
                CommPortIdentifier.addPortName(var5, var2, this);
            }
        }

    }

    private boolean registerSpecifiedPorts(int var1) {
        String var2 = null;

        try {
            String var3 = System.getProperty("java.ext.dirs") + System.getProperty("file.separator");
            FileInputStream var4 = new FileInputStream(var3 + "gnu.io.rxtx.properties");
            Properties var5 = new Properties();
            var5.load(var4);
            System.setProperties(var5);
            Iterator var6 = var5.keySet().iterator();

            while (var6.hasNext()) {
                String var7 = (String) var6.next();
                System.setProperty(var7, var5.getProperty(var7));
            }
        } catch (Exception var8) {
            ;
        }

        switch (var1) {
            case 1:
                if ((var2 = System.getProperty("gnu.io.rxtx.SerialPorts")) == null) {
                    var2 = System.getProperty("gnu.io.SerialPorts");
                }
                break;
            case 2:
                if ((var2 = System.getProperty("gnu.io.rxtx.ParallelPorts")) == null) {
                    var2 = System.getProperty("gnu.io.ParallelPorts");
                }
        }

        if (var2 != null) {
            this.addSpecifiedPorts(var2, var1);
            return true;
        } else {
            return false;
        }
    }

    private void registerScannedPorts(int var1) {
        String[] var2;
        String[] var3;
        String[] var9;
        if (this.osName.equals("Windows CE")) {
            var3 = new String[]{"COM1:", "COM2:", "COM3:", "COM4:", "COM5:", "COM6:", "COM7:", "COM8:"};
            var2 = var3;
        } else {
            int var4;
            if (this.osName.toLowerCase().indexOf("windows") != -1) {
                var3 = new String[259];

                for (var4 = 1; var4 <= 256; ++var4) {
                    var3[var4 - 1] = new String("COM" + var4);
                }

                for (var4 = 1; var4 <= 3; ++var4) {
                    var3[var4 + 255] = new String("LPT" + var4);
                }

                var2 = var3;
            } else if (!this.osName.equals("Solaris") && !this.osName.equals("SunOS")) {
                File var7 = new File(this.deviceDirectory);
                var9 = var7.list();
                var2 = var9;
            } else {
                var3 = new String[2];
                byte var8 = 0;
                File var5 = null;
                var5 = new File("/dev/term");
                if (var5.list().length > 0) {
                    ;
                }

                var4 = var8 + 1;
                var3[var8] = new String("term/");
                String[] var6 = new String[var4];
                --var4;

                while (var4 >= 0) {
                    var6[var4] = var3[var4];
                    --var4;
                }

                var2 = var6;
            }
        }

        if (var2 != null) {
            var3 = new String[0];
            switch (var1) {
                case 1:
                    if (this.osName.equals("Linux")) {
                        var9 = new String[]{"ttyS", "ttySA", "ttyUSB"};
                        var3 = var9;
                    } else if (this.osName.equals("Linux-all-ports")) {
                        var9 = new String[]{"comx", "holter", "modem", "rfcomm", "ttyircomm", "ttycosa0c", "ttycosa1c", "ttyC", "ttyCH", "ttyD", "ttyE", "ttyF", "ttyH", "ttyI", "ttyL", "ttyM", "ttyMX", "ttyP", "ttyR", "ttyS", "ttySI", "ttySR", "ttyT", "ttyUSB", "ttyV", "ttyW", "ttyX"};
                        var3 = var9;
                    } else if (this.osName.toLowerCase().indexOf("qnx") != -1) {
                        var9 = new String[]{"ser"};
                        var3 = var9;
                    } else if (this.osName.equals("Irix")) {
                        var9 = new String[]{"ttyc", "ttyd", "ttyf", "ttym", "ttyq", "tty4d", "tty4f", "midi", "us"};
                        var3 = var9;
                    } else if (this.osName.equals("FreeBSD")) {
                        var9 = new String[]{"ttyd", "cuaa", "ttyA", "cuaA", "ttyD", "cuaD", "ttyE", "cuaE", "ttyF", "cuaF", "ttyR", "cuaR", "stl"};
                        var3 = var9;
                    } else if (this.osName.equals("NetBSD")) {
                        var9 = new String[]{"tty0"};
                        var3 = var9;
                    } else if (!this.osName.equals("Solaris") && !this.osName.equals("SunOS")) {
                        if (this.osName.equals("HP-UX")) {
                            var9 = new String[]{"tty0p", "tty1p"};
                            var3 = var9;
                        } else if (!this.osName.equals("UnixWare") && !this.osName.equals("OpenUNIX")) {
                            if (this.osName.equals("OpenServer")) {
                                var9 = new String[]{"tty1A", "tty2A", "tty3A", "tty4A", "tty5A", "tty6A", "tty7A", "tty8A", "tty9A", "tty10A", "tty11A", "tty12A", "tty13A", "tty14A", "tty15A", "tty16A", "ttyu1A", "ttyu2A", "ttyu3A", "ttyu4A", "ttyu5A", "ttyu6A", "ttyu7A", "ttyu8A", "ttyu9A", "ttyu10A", "ttyu11A", "ttyu12A", "ttyu13A", "ttyu14A", "ttyu15A", "ttyu16A"};
                                var3 = var9;
                            } else if (!this.osName.equals("Compaq\'s Digital UNIX") && !this.osName.equals("OSF1")) {
                                if (this.osName.equals("BeOS")) {
                                    var9 = new String[]{"serial"};
                                    var3 = var9;
                                } else if (this.osName.equals("Mac OS X")) {
                                    var9 = new String[]{"cu.KeyUSA28X191.", "tty.KeyUSA28X191.", "cu.KeyUSA28X181.", "tty.KeyUSA28X181.", "cu.KeyUSA19181.", "tty.KeyUSA19181."};
                                    var3 = var9;
                                } else if (this.osName.toLowerCase().indexOf("windows") != -1) {
                                    var9 = new String[]{"COM"};
                                    var3 = var9;
                                }
                            } else {
                                var9 = new String[]{"tty0"};
                                var3 = var9;
                            }
                        } else {
                            var9 = new String[]{"tty00s", "tty01s", "tty02s", "tty03s"};
                            var3 = var9;
                        }
                    } else {
                        var9 = new String[]{"term/", "cua/"};
                        var3 = var9;
                    }
                    break;
                case 2:
                    if (this.osName.equals("Linux")) {
                        var9 = new String[]{"lp"};
                        var3 = var9;
                    } else if (this.osName.equals("FreeBSD")) {
                        var9 = new String[]{"lpt"};
                        var3 = var9;
                    } else if (this.osName.toLowerCase().indexOf("windows") != -1) {
                        var9 = new String[]{"LPT"};
                        var3 = var9;
                    } else {
                        var9 = new String[0];
                        var3 = var9;
                    }
            }

            this.registerValidPorts(var2, var3, var1);
        }
    }

    public CommPort getCommPort(String var1, int var2) {
        try {
            switch (var2) {
                case 1:
                    if (this.osName.toLowerCase().indexOf("windows") == -1) {
                        return new RXTXPort(var1);
                    }

                    return new RXTXPort(this.deviceDirectory + var1);
                case 2:
                    return new LPRPort(var1);
            }
        } catch (PortInUseException var4) {
            ;
        }

        return null;
    }

    public void Report(String var1) {
        System.out.println(var1);
    }

    static {
        System.loadLibrary("rxtxSerial");
        String var0 = RXTXVersion.getVersion();

        String var1;
        try {
            var1 = RXTXVersion.nativeGetVersion();
        } catch (Error var3) {
            var1 = nativeGetVersion();
        }

        System.out.println("Stable Library");
        System.out.println("=========================================");
        System.out.println("Native lib Version = " + var1);
        System.out.println("Java lib Version   = " + var0);
        if (!var0.equals(var1)) {
            System.out.println("WARNING:  RXTX Version mismatch\n\tJar version = " + var0 + "\n\tnative lib Version = " + var1);
        }

    }
}

