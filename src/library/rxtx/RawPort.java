package library.rxtx;

import java.util.TooManyListenersException;

abstract class RawPort extends CommPort {
    public static final int DATABITS_5 = 5;
    public static final int DATABITS_6 = 6;
    public static final int DATABITS_7 = 7;
    public static final int DATABITS_8 = 8;
    public static final int PARITY_NONE = 0;
    public static final int PARITY_ODD = 1;
    public static final int PARITY_EVEN = 2;
    public static final int PARITY_MARK = 3;
    public static final int PARITY_SPACE = 4;
    public static final int STOPBITS_1 = 1;
    public static final int STOPBITS_1_5 = 0;
    public static final int STOPBITS_2 = 2;
    public static final int FLOWCONTROL_NONE = 0;
    public static final int FLOWCONTROL_RTSCTS_IN = 1;
    public static final int FLOWCONTROL_RTSCTS_OUT = 2;
    public static final int FLOWCONTROL_XONXOFF_IN = 4;
    public static final int FLOWCONTROL_XONXOFF_OUT = 8;
    public static final int WRITE_SIZE = 8;
    public static final int IO_PORT = 888;

    RawPort() {
    }

    public abstract void setRawPortParams(int var1, int var2, int var3, int var4) throws UnsupportedCommOperationException;

    public abstract void addEventListener(RawPortEventListener var1) throws TooManyListenersException;

    public abstract void removeEventListener();
}
