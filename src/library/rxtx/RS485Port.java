package library.rxtx;

import java.util.TooManyListenersException;

abstract class RS485Port extends CommPort {
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

    RS485Port() {
    }

    public abstract void setRS485PortParams(int var1, int var2, int var3, int var4) throws UnsupportedCommOperationException;

    public abstract int getBaudRate();

    public abstract int getDataBits();

    public abstract int getStopBits();

    public abstract int getParity();

    public abstract void setFlowControlMode(int var1) throws UnsupportedCommOperationException;

    public abstract int getFlowControlMode();

    public abstract boolean isDTR();

    public abstract void setDTR(boolean var1);

    public abstract void setRTS(boolean var1);

    public abstract boolean isCTS();

    public abstract boolean isDSR();

    public abstract boolean isCD();

    public abstract boolean isRI();

    public abstract boolean isRTS();

    public abstract void sendBreak(int var1);

    public abstract void addEventListener(RS485PortEventListener var1) throws TooManyListenersException;

    public abstract void removeEventListener();

    public abstract void notifyOnDataAvailable(boolean var1);

    public abstract void notifyOnOutputEmpty(boolean var1);

    public abstract void notifyOnCTS(boolean var1);

    public abstract void notifyOnDSR(boolean var1);

    public abstract void notifyOnRingIndicator(boolean var1);

    public abstract void notifyOnCarrierDetect(boolean var1);

    public abstract void notifyOnOverrunError(boolean var1);

    public abstract void notifyOnParityError(boolean var1);

    public abstract void notifyOnFramingError(boolean var1);

    public abstract void notifyOnBreakInterrupt(boolean var1);
}
