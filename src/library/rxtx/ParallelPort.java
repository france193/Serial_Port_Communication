package library.rxtx;

import java.util.TooManyListenersException;

public abstract class ParallelPort extends CommPort {
    public static final int LPT_MODE_ANY = 0;
    public static final int LPT_MODE_SPP = 1;
    public static final int LPT_MODE_PS2 = 2;
    public static final int LPT_MODE_EPP = 3;
    public static final int LPT_MODE_ECP = 4;
    public static final int LPT_MODE_NIBBLE = 5;

    public ParallelPort() {
    }

    public abstract int getMode();

    public abstract int setMode(int var1) throws UnsupportedCommOperationException;

    public abstract void restart();

    public abstract void suspend();

    public abstract boolean isPaperOut();

    public abstract boolean isPrinterBusy();

    public abstract boolean isPrinterError();

    public abstract boolean isPrinterSelected();

    public abstract boolean isPrinterTimedOut();

    public abstract int getOutputBufferFree();

    public abstract void addEventListener(ParallelPortEventListener var1) throws TooManyListenersException;

    public abstract void removeEventListener();

    public abstract void notifyOnError(boolean var1);

    public abstract void notifyOnBuffer(boolean var1);
}
