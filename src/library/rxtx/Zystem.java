package library.rxtx;

import java.io.RandomAccessFile;
import java.util.logging.Logger;

public class Zystem {
    public static final int SILENT_MODE = 0;
    public static final int FILE_MODE = 1;
    public static final int NET_MODE = 2;
    public static final int MEX_MODE = 3;
    public static final int PRINT_MODE = 4;
    public static final int J2EE_MSG_MODE = 5;
    public static final int J2SE_LOG_MODE = 6;
    static int mode = 0;
    private static String target;

    public Zystem(int var1) throws UnSupportedLoggerException {
        mode = var1;
        this.startLogger("asdf");
    }

    public Zystem() throws UnSupportedLoggerException {
        String var1 = System.getProperty("gnu.io.log.mode");
        if (var1 != null) {
            if ("SILENT_MODE".equals(var1)) {
                mode = 0;
            } else if ("FILE_MODE".equals(var1)) {
                mode = 1;
            } else if ("NET_MODE".equals(var1)) {
                mode = 2;
            } else if ("MEX_MODE".equals(var1)) {
                mode = 3;
            } else if ("PRINT_MODE".equals(var1)) {
                mode = 4;
            } else if ("J2EE_MSG_MODE".equals(var1)) {
                mode = 5;
            } else if ("J2SE_LOG_MODE".equals(var1)) {
                mode = 6;
            } else {
                try {
                    mode = Integer.parseInt(var1);
                } catch (NumberFormatException var3) {
                    mode = 0;
                }
            }
        } else {
            mode = 0;
        }

        this.startLogger("asdf");
    }

    public void startLogger() throws UnSupportedLoggerException {
        if (mode != 0 && mode != 4) {
            throw new UnSupportedLoggerException("Target Not Allowed");
        }
    }

    public void startLogger(String var1) throws UnSupportedLoggerException {
        target = var1;
    }

    public void finalize() {
        mode = 0;
        target = null;
    }

    public void filewrite(String var1) {
        try {
            RandomAccessFile var2 = new RandomAccessFile(target, "rw");
            var2.seek(var2.length());
            var2.writeBytes(var1);
            var2.close();
        } catch (Exception var3) {
            System.out.println("Debug output file write failed");
        }

    }

    public boolean report(String var1) {
        if (mode != 2) {
            if (mode == 4) {
                System.out.println(var1);
                return true;
            }

            if (mode != 3) {
                if (mode == 0) {
                    return true;
                }

                if (mode == 1) {
                    this.filewrite(var1);
                } else {
                    if (mode == 5) {
                        return false;
                    }

                    if (mode == 6) {
                        Logger.getLogger("gnu.io").fine(var1);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean reportln() {
        if (mode != 2) {
            if (mode == 4) {
                System.out.println();
                return true;
            }

            if (mode != 3) {
                if (mode == 0) {
                    return true;
                }

                if (mode == 1) {
                    this.filewrite("\n");
                } else if (mode == 5) {
                    return false;
                }
            }
        }

        return false;
    }

    public boolean reportln(String var1) {
        if (mode != 2) {
            if (mode == 4) {
                System.out.println(var1);
                return true;
            }

            if (mode != 3) {
                if (mode == 0) {
                    return true;
                }

                if (mode == 1) {
                    this.filewrite(var1 + "\n");
                } else {
                    if (mode == 5) {
                        return false;
                    }

                    if (mode == 6) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
