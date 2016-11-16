package library.rxtx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

public final class RXTXPort extends SerialPort {
    protected static final boolean debug = false;
    protected static final boolean debug_read = false;
    protected static final boolean debug_read_results = false;
    protected static final boolean debug_write = false;
    protected static final boolean debug_events = false;
    protected static final boolean debug_verbose = false;
    private static Zystem z;
    boolean MonitorThreadAlive = false;
    int IOLocked = 0;
    private int fd = 0;
    long eis = 0L;
    int pid = 0;
    static boolean dsrFlag;
    private final RXTXPort.SerialOutputStream out = new RXTXPort.SerialOutputStream();
    private final RXTXPort.SerialInputStream in = new RXTXPort.SerialInputStream();
    private int speed = 9600;
    private int dataBits = 8;
    private int stopBits = 1;
    private int parity = 0;
    private int flowmode = 0;
    private int timeout;
    private int threshold = 0;
    private int InputBuffer = 0;
    private int OutputBuffer = 0;
    private SerialPortEventListener SPEventListener;
    private RXTXPort.MonitorThread monThread;
    boolean monThreadisInterrupted = true;
    boolean MonitorThreadLock = true;
    boolean closeLock = false;

    private static native void Initialize();

    public RXTXPort(String var1) throws PortInUseException {
        this.fd = this.open(var1);
        this.name = var1;
        this.MonitorThreadLock = true;
        this.monThread = new RXTXPort.MonitorThread();
        this.monThread.start();
        this.waitForTheNativeCodeSilly();
        this.MonitorThreadAlive = true;
        this.timeout = -1;
    }

    private synchronized native int open(String var1) throws PortInUseException;

    public OutputStream getOutputStream() {
        return this.out;
    }

    public InputStream getInputStream() {
        return this.in;
    }

    private native int nativeGetParity(int var1);

    private native int nativeGetFlowControlMode(int var1);

    public synchronized void setSerialPortParams(int var1, int var2, int var3, int var4) throws UnsupportedCommOperationException {
        if (this.nativeSetSerialPortParams(var1, var2, var3, var4)) {
            throw new UnsupportedCommOperationException("Invalid Parameter");
        } else {
            this.speed = var1;
            if (var3 == 3) {
                this.dataBits = 5;
            } else {
                this.dataBits = var2;
            }

            this.stopBits = var3;
            this.parity = var4;
            z.reportln("RXTXPort:setSerialPortParams(" + var1 + " " + var2 + " " + var3 + " " + var4 + ") returning");
        }
    }

    private native boolean nativeSetSerialPortParams(int var1, int var2, int var3, int var4) throws UnsupportedCommOperationException;

    public int getBaudRate() {
        return this.speed;
    }

    public int getDataBits() {
        return this.dataBits;
    }

    public int getStopBits() {
        return this.stopBits;
    }

    public int getParity() {
        return this.parity;
    }

    public void setFlowControlMode(int var1) {
        if (!this.monThreadisInterrupted) {
            try {
                this.setflowcontrol(var1);
            } catch (IOException var3) {
                var3.printStackTrace();
                return;
            }

            this.flowmode = var1;
        }
    }

    public int getFlowControlMode() {
        return this.flowmode;
    }

    native void setflowcontrol(int var1) throws IOException;

    public void enableReceiveFraming(int var1) throws UnsupportedCommOperationException {
        throw new UnsupportedCommOperationException("Not supported");
    }

    public void disableReceiveFraming() {
    }

    public boolean isReceiveFramingEnabled() {
        return false;
    }

    public int getReceiveFramingByte() {
        return 0;
    }

    public native int NativegetReceiveTimeout();

    private native boolean NativeisReceiveTimeoutEnabled();

    private native void NativeEnableReceiveTimeoutThreshold(int var1, int var2, int var3);

    public void disableReceiveTimeout() {
        this.timeout = -1;
        this.NativeEnableReceiveTimeoutThreshold(this.timeout, this.threshold, this.InputBuffer);
    }

    public void enableReceiveTimeout(int var1) {
        if (var1 >= 0) {
            this.timeout = var1;
            this.NativeEnableReceiveTimeoutThreshold(var1, this.threshold, this.InputBuffer);
        } else {
            throw new IllegalArgumentException("Unexpected negative timeout value");
        }
    }

    public boolean isReceiveTimeoutEnabled() {
        return this.NativeisReceiveTimeoutEnabled();
    }

    public int getReceiveTimeout() {
        return this.NativegetReceiveTimeout();
    }

    public void enableReceiveThreshold(int var1) {
        if (var1 >= 0) {
            this.threshold = var1;
            this.NativeEnableReceiveTimeoutThreshold(this.timeout, this.threshold, this.InputBuffer);
        } else {
            throw new IllegalArgumentException("Unexpected negative threshold value");
        }
    }

    public void disableReceiveThreshold() {
        this.enableReceiveThreshold(0);
    }

    public int getReceiveThreshold() {
        return this.threshold;
    }

    public boolean isReceiveThresholdEnabled() {
        return this.threshold > 0;
    }

    public void setInputBufferSize(int var1) {
        if (var1 < 0) {
            throw new IllegalArgumentException("Unexpected negative buffer size value");
        } else {
            this.InputBuffer = var1;
        }
    }

    public int getInputBufferSize() {
        return this.InputBuffer;
    }

    public void setOutputBufferSize(int var1) {
        if (var1 < 0) {
            throw new IllegalArgumentException("Unexpected negative buffer size value");
        } else {
            this.OutputBuffer = var1;
        }
    }

    public int getOutputBufferSize() {
        return this.OutputBuffer;
    }

    public native boolean isDTR();

    public native void setDTR(boolean var1);

    public native void setRTS(boolean var1);

    private native void setDSR(boolean var1);

    public native boolean isCTS();

    public native boolean isDSR();

    public native boolean isCD();

    public native boolean isRI();

    public native boolean isRTS();

    public native void sendBreak(int var1);

    protected native void writeByte(int var1, boolean var2) throws IOException;

    protected native void writeArray(byte[] var1, int var2, int var3, boolean var4) throws IOException;

    protected native boolean nativeDrain(boolean var1) throws IOException;

    protected native int nativeavailable() throws IOException;

    protected native int readByte() throws IOException;

    protected native int readArray(byte[] var1, int var2, int var3) throws IOException;

    protected native int readTerminatedArray(byte[] var1, int var2, int var3, byte[] var4) throws IOException;

    native void eventLoop();

    private native void interruptEventLoop();

    public boolean checkMonitorThread() {
        return this.monThread != null ? this.monThreadisInterrupted : true;
    }

    public boolean sendEvent(int var1, boolean var2) {
        if (this.fd != 0 && this.SPEventListener != null && this.monThread != null) {
            switch (var1) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                default:
                    switch (var1) {
                        case 1:
                            if (!this.monThread.Data) {
                                return false;
                            }
                            break;
                        case 2:
                            if (!this.monThread.Output) {
                                return false;
                            }
                            break;
                        case 3:
                            if (!this.monThread.CTS) {
                                return false;
                            }
                            break;
                        case 4:
                            if (!this.monThread.DSR) {
                                return false;
                            }
                            break;
                        case 5:
                            if (!this.monThread.RI) {
                                return false;
                            }
                            break;
                        case 6:
                            if (!this.monThread.CD) {
                                return false;
                            }
                            break;
                        case 7:
                            if (!this.monThread.OE) {
                                return false;
                            }
                            break;
                        case 8:
                            if (!this.monThread.PE) {
                                return false;
                            }
                            break;
                        case 9:
                            if (!this.monThread.FE) {
                                return false;
                            }
                            break;
                        case 10:
                            if (!this.monThread.BI) {
                                return false;
                            }
                            break;
                        default:
                            System.err.println("unknown event: " + var1);
                            return false;
                    }

                    SerialPortEvent var3 = new SerialPortEvent(this, var1, !var2, var2);
                    if (this.monThreadisInterrupted) {
                        return true;
                    } else {
                        if (this.SPEventListener != null) {
                            this.SPEventListener.serialEvent(var3);
                        }

                        return this.fd == 0 || this.SPEventListener == null || this.monThread == null;
                    }
            }
        } else {
            return true;
        }
    }

    public void addEventListener(SerialPortEventListener var1) throws TooManyListenersException {
        if (this.SPEventListener != null) {
            throw new TooManyListenersException();
        } else {
            this.SPEventListener = var1;
            if (!this.MonitorThreadAlive) {
                this.MonitorThreadLock = true;
                this.monThread = new RXTXPort.MonitorThread();
                this.monThread.start();
                this.waitForTheNativeCodeSilly();
                this.MonitorThreadAlive = true;
            }

        }
    }

    public void removeEventListener() {
        this.waitForTheNativeCodeSilly();
        if (this.monThreadisInterrupted) {
            z.reportln("\tRXTXPort:removeEventListener() already interrupted");
            this.monThread = null;
            this.SPEventListener = null;
        } else {
            if (this.monThread != null && this.monThread.isAlive()) {
                this.monThreadisInterrupted = true;
                this.interruptEventLoop();

                try {
                    this.monThread.join(1000L);
                } catch (Exception var3) {
                    var3.printStackTrace();
                }

                while (this.monThread.isAlive()) {
                    try {
                        this.monThread.join(1000L);
                        Thread.sleep(1000L);
                    } catch (Exception var2) {
                        ;
                    }
                }
            }

            this.monThread = null;
            this.SPEventListener = null;
            this.MonitorThreadLock = false;
            this.MonitorThreadAlive = false;
            this.monThreadisInterrupted = true;
            z.reportln("RXTXPort:removeEventListener() returning");
        }
    }

    protected void waitForTheNativeCodeSilly() {
        while (this.MonitorThreadLock) {
            try {
                Thread.sleep(5L);
            } catch (Exception var2) {
                ;
            }
        }

    }

    private native void nativeSetEventFlag(int var1, int var2, boolean var3);

    public void notifyOnDataAvailable(boolean var1) {
        this.waitForTheNativeCodeSilly();
        this.MonitorThreadLock = true;
        this.nativeSetEventFlag(this.fd, 1, var1);
        this.monThread.Data = var1;
        this.MonitorThreadLock = false;
    }

    public void notifyOnOutputEmpty(boolean var1) {
        this.waitForTheNativeCodeSilly();
        this.MonitorThreadLock = true;
        this.nativeSetEventFlag(this.fd, 2, var1);
        this.monThread.Output = var1;
        this.MonitorThreadLock = false;
    }

    public void notifyOnCTS(boolean var1) {
        this.waitForTheNativeCodeSilly();
        this.MonitorThreadLock = true;
        this.nativeSetEventFlag(this.fd, 3, var1);
        this.monThread.CTS = var1;
        this.MonitorThreadLock = false;
    }

    public void notifyOnDSR(boolean var1) {
        this.waitForTheNativeCodeSilly();
        this.MonitorThreadLock = true;
        this.nativeSetEventFlag(this.fd, 4, var1);
        this.monThread.DSR = var1;
        this.MonitorThreadLock = false;
    }

    public void notifyOnRingIndicator(boolean var1) {
        this.waitForTheNativeCodeSilly();
        this.MonitorThreadLock = true;
        this.nativeSetEventFlag(this.fd, 5, var1);
        this.monThread.RI = var1;
        this.MonitorThreadLock = false;
    }

    public void notifyOnCarrierDetect(boolean var1) {
        this.waitForTheNativeCodeSilly();
        this.MonitorThreadLock = true;
        this.nativeSetEventFlag(this.fd, 6, var1);
        this.monThread.CD = var1;
        this.MonitorThreadLock = false;
    }

    public void notifyOnOverrunError(boolean var1) {
        this.waitForTheNativeCodeSilly();
        this.MonitorThreadLock = true;
        this.nativeSetEventFlag(this.fd, 7, var1);
        this.monThread.OE = var1;
        this.MonitorThreadLock = false;
    }

    public void notifyOnParityError(boolean var1) {
        this.waitForTheNativeCodeSilly();
        this.MonitorThreadLock = true;
        this.nativeSetEventFlag(this.fd, 8, var1);
        this.monThread.PE = var1;
        this.MonitorThreadLock = false;
    }

    public void notifyOnFramingError(boolean var1) {
        this.waitForTheNativeCodeSilly();
        this.MonitorThreadLock = true;
        this.nativeSetEventFlag(this.fd, 9, var1);
        this.monThread.FE = var1;
        this.MonitorThreadLock = false;
    }

    public void notifyOnBreakInterrupt(boolean var1) {
        this.waitForTheNativeCodeSilly();
        this.MonitorThreadLock = true;
        this.nativeSetEventFlag(this.fd, 10, var1);
        this.monThread.BI = var1;
        this.MonitorThreadLock = false;
    }

    private native void nativeClose(String var1);

    public synchronized void close() {
        if (!this.closeLock) {
            this.closeLock = true;

            while (this.IOLocked > 0) {
                try {
                    Thread.sleep(500L);
                } catch (Exception var2) {
                    ;
                }
            }

            if (this.fd <= 0) {
                z.reportln("RXTXPort:close detected bad File Descriptor");
            } else {
                this.setDTR(false);
                this.setDSR(false);
                if (!this.monThreadisInterrupted) {
                    this.removeEventListener();
                }

                this.nativeClose(this.name);
                super.close();
                this.fd = 0;
                this.closeLock = false;
            }
        }
    }

    protected void finalize() {
        if (this.fd > 0) {
            this.close();
        }

        z.finalize();
    }

    /**
     * @deprecated
     */
    public void setRcvFifoTrigger(int var1) {
    }

    private static native void nativeStaticSetSerialPortParams(String var0, int var1, int var2, int var3, int var4) throws UnsupportedCommOperationException;

    private static native boolean nativeStaticSetDSR(String var0, boolean var1) throws UnsupportedCommOperationException;

    private static native boolean nativeStaticSetDTR(String var0, boolean var1) throws UnsupportedCommOperationException;

    private static native boolean nativeStaticSetRTS(String var0, boolean var1) throws UnsupportedCommOperationException;

    private static native boolean nativeStaticIsDSR(String var0) throws UnsupportedCommOperationException;

    private static native boolean nativeStaticIsDTR(String var0) throws UnsupportedCommOperationException;

    private static native boolean nativeStaticIsRTS(String var0) throws UnsupportedCommOperationException;

    private static native boolean nativeStaticIsCTS(String var0) throws UnsupportedCommOperationException;

    private static native boolean nativeStaticIsCD(String var0) throws UnsupportedCommOperationException;

    private static native boolean nativeStaticIsRI(String var0) throws UnsupportedCommOperationException;

    private static native int nativeStaticGetBaudRate(String var0) throws UnsupportedCommOperationException;

    private static native int nativeStaticGetDataBits(String var0) throws UnsupportedCommOperationException;

    private static native int nativeStaticGetParity(String var0) throws UnsupportedCommOperationException;

    private static native int nativeStaticGetStopBits(String var0) throws UnsupportedCommOperationException;

    private native byte nativeGetParityErrorChar() throws UnsupportedCommOperationException;

    private native boolean nativeSetParityErrorChar(byte var1) throws UnsupportedCommOperationException;

    private native byte nativeGetEndOfInputChar() throws UnsupportedCommOperationException;

    private native boolean nativeSetEndOfInputChar(byte var1) throws UnsupportedCommOperationException;

    private native boolean nativeSetUartType(String var1, boolean var2) throws UnsupportedCommOperationException;

    native String nativeGetUartType() throws UnsupportedCommOperationException;

    private native boolean nativeSetBaudBase(int var1) throws UnsupportedCommOperationException;

    private native int nativeGetBaudBase() throws UnsupportedCommOperationException;

    private native boolean nativeSetDivisor(int var1) throws UnsupportedCommOperationException;

    private native int nativeGetDivisor() throws UnsupportedCommOperationException;

    private native boolean nativeSetLowLatency() throws UnsupportedCommOperationException;

    private native boolean nativeGetLowLatency() throws UnsupportedCommOperationException;

    private native boolean nativeSetCallOutHangup(boolean var1) throws UnsupportedCommOperationException;

    private native boolean nativeGetCallOutHangup() throws UnsupportedCommOperationException;

    private native boolean nativeClearCommInput() throws UnsupportedCommOperationException;

    public static int staticGetBaudRate(String var0) throws UnsupportedCommOperationException {
        return nativeStaticGetBaudRate(var0);
    }

    public static int staticGetDataBits(String var0) throws UnsupportedCommOperationException {
        return nativeStaticGetDataBits(var0);
    }

    public static int staticGetParity(String var0) throws UnsupportedCommOperationException {
        return nativeStaticGetParity(var0);
    }

    public static int staticGetStopBits(String var0) throws UnsupportedCommOperationException {
        return nativeStaticGetStopBits(var0);
    }

    public static void staticSetSerialPortParams(String var0, int var1, int var2, int var3, int var4) throws UnsupportedCommOperationException {
        nativeStaticSetSerialPortParams(var0, var1, var2, var3, var4);
    }

    public static boolean staticSetDSR(String var0, boolean var1) throws UnsupportedCommOperationException {
        return nativeStaticSetDSR(var0, var1);
    }

    public static boolean staticSetDTR(String var0, boolean var1) throws UnsupportedCommOperationException {
        return nativeStaticSetDTR(var0, var1);
    }

    public static boolean staticSetRTS(String var0, boolean var1) throws UnsupportedCommOperationException {
        return nativeStaticSetRTS(var0, var1);
    }

    public static boolean staticIsRTS(String var0) throws UnsupportedCommOperationException {
        return nativeStaticIsRTS(var0);
    }

    public static boolean staticIsCD(String var0) throws UnsupportedCommOperationException {
        return nativeStaticIsCD(var0);
    }

    public static boolean staticIsCTS(String var0) throws UnsupportedCommOperationException {
        return nativeStaticIsCTS(var0);
    }

    public static boolean staticIsDSR(String var0) throws UnsupportedCommOperationException {
        return nativeStaticIsDSR(var0);
    }

    public static boolean staticIsDTR(String var0) throws UnsupportedCommOperationException {
        return nativeStaticIsDTR(var0);
    }

    public static boolean staticIsRI(String var0) throws UnsupportedCommOperationException {
        return nativeStaticIsRI(var0);
    }

    public byte getParityErrorChar() throws UnsupportedCommOperationException {
        byte var1 = this.nativeGetParityErrorChar();
        return var1;
    }

    public boolean setParityErrorChar(byte var1) throws UnsupportedCommOperationException {
        return this.nativeSetParityErrorChar(var1);
    }

    public byte getEndOfInputChar() throws UnsupportedCommOperationException {
        byte var1 = this.nativeGetEndOfInputChar();
        return var1;
    }

    public boolean setEndOfInputChar(byte var1) throws UnsupportedCommOperationException {
        return this.nativeSetEndOfInputChar(var1);
    }

    public boolean setUARTType(String var1, boolean var2) throws UnsupportedCommOperationException {
        return this.nativeSetUartType(var1, var2);
    }

    public String getUARTType() throws UnsupportedCommOperationException {
        return this.nativeGetUartType();
    }

    public boolean setBaudBase(int var1) throws UnsupportedCommOperationException, IOException {
        return this.nativeSetBaudBase(var1);
    }

    public int getBaudBase() throws UnsupportedCommOperationException, IOException {
        return this.nativeGetBaudBase();
    }

    public boolean setDivisor(int var1) throws UnsupportedCommOperationException, IOException {
        return this.nativeSetDivisor(var1);
    }

    public int getDivisor() throws UnsupportedCommOperationException, IOException {
        return this.nativeGetDivisor();
    }

    public boolean setLowLatency() throws UnsupportedCommOperationException {
        return this.nativeSetLowLatency();
    }

    public boolean getLowLatency() throws UnsupportedCommOperationException {
        return this.nativeGetLowLatency();
    }

    public boolean setCallOutHangup(boolean var1) throws UnsupportedCommOperationException {
        return this.nativeSetCallOutHangup(var1);
    }

    public boolean getCallOutHangup() throws UnsupportedCommOperationException {
        return this.nativeGetCallOutHangup();
    }

    public boolean clearCommInput() throws UnsupportedCommOperationException {
        return this.nativeClearCommInput();
    }

    static {
        try {
            z = new Zystem();
        } catch (Exception var1) {
            ;
        }

        System.loadLibrary("rxtxSerial");
        Initialize();
        dsrFlag = false;
    }

    class MonitorThread extends Thread {
        private volatile boolean CTS = false;
        private volatile boolean DSR = false;
        private volatile boolean RI = false;
        private volatile boolean CD = false;
        private volatile boolean OE = false;
        private volatile boolean PE = false;
        private volatile boolean FE = false;
        private volatile boolean BI = false;
        private volatile boolean Data = false;
        private volatile boolean Output = false;

        MonitorThread() {
        }

        public void run() {
            RXTXPort.this.monThreadisInterrupted = false;
            RXTXPort.this.eventLoop();
        }

        protected void finalize() throws Throwable {
        }
    }

    class SerialInputStream extends InputStream {
        SerialInputStream() {
        }

        public synchronized int read() throws IOException {
            if (RXTXPort.this.fd == 0) {
                throw new IOException();
            } else {
                if (RXTXPort.this.monThreadisInterrupted) {
                    RXTXPort.z.reportln("+++++++++ read() monThreadisInterrupted");
                }

                ++RXTXPort.this.IOLocked;
                RXTXPort.this.waitForTheNativeCodeSilly();

                int var2;
                try {
                    int var1 = RXTXPort.this.readByte();
                    var2 = var1;
                } finally {
                    --RXTXPort.this.IOLocked;
                }

                return var2;
            }
        }

        public synchronized int read(byte[] var1) throws IOException {
            if (RXTXPort.this.monThreadisInterrupted) {
                return 0;
            } else {
                ++RXTXPort.this.IOLocked;
                RXTXPort.this.waitForTheNativeCodeSilly();

                int var3;
                try {
                    int var2 = this.read(var1, 0, var1.length);
                    var3 = var2;
                } finally {
                    --RXTXPort.this.IOLocked;
                }

                return var3;
            }
        }

        public synchronized int read(byte[] var1, int var2, int var3) throws IOException {
            if (RXTXPort.this.fd == 0) {
                RXTXPort.z.reportln("+++++++ IOException()\n");
                throw new IOException();
            } else if (var1 == null) {
                RXTXPort.z.reportln("+++++++ NullPointerException()\n");
                throw new NullPointerException();
            } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
                if (var3 == 0) {
                    return 0;
                } else {
                    int var5;
                    int var6;
                    if (RXTXPort.this.threshold == 0) {
                        var6 = RXTXPort.this.nativeavailable();
                        if (var6 == 0) {
                            var5 = 1;
                        } else {
                            var5 = Math.min(var3, var6);
                        }
                    } else {
                        var5 = Math.min(var3, RXTXPort.this.threshold);
                    }

                    if (RXTXPort.this.monThreadisInterrupted) {
                        return 0;
                    } else {
                        ++RXTXPort.this.IOLocked;
                        RXTXPort.this.waitForTheNativeCodeSilly();

                        try {
                            int var4 = RXTXPort.this.readArray(var1, var2, var5);
                            var6 = var4;
                        } finally {
                            --RXTXPort.this.IOLocked;
                        }

                        return var6;
                    }
                }
            } else {
                RXTXPort.z.reportln("+++++++ IndexOutOfBoundsException()\n");
                throw new IndexOutOfBoundsException();
            }
        }

        public synchronized int read(byte[] var1, int var2, int var3, byte[] var4) throws IOException {
            if (RXTXPort.this.fd == 0) {
                RXTXPort.z.reportln("+++++++ IOException()\n");
                throw new IOException();
            } else if (var1 == null) {
                RXTXPort.z.reportln("+++++++ NullPointerException()\n");
                throw new NullPointerException();
            } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
                if (var3 == 0) {
                    return 0;
                } else {
                    int var6;
                    int var7;
                    if (RXTXPort.this.threshold == 0) {
                        var7 = RXTXPort.this.nativeavailable();
                        if (var7 == 0) {
                            var6 = 1;
                        } else {
                            var6 = Math.min(var3, var7);
                        }
                    } else {
                        var6 = Math.min(var3, RXTXPort.this.threshold);
                    }

                    if (RXTXPort.this.monThreadisInterrupted) {
                        return 0;
                    } else {
                        ++RXTXPort.this.IOLocked;
                        RXTXPort.this.waitForTheNativeCodeSilly();

                        try {
                            int var5 = RXTXPort.this.readTerminatedArray(var1, var2, var6, var4);
                            var7 = var5;
                        } finally {
                            --RXTXPort.this.IOLocked;
                        }

                        return var7;
                    }
                }
            } else {
                RXTXPort.z.reportln("+++++++ IndexOutOfBoundsException()\n");
                throw new IndexOutOfBoundsException();
            }
        }

        public synchronized int available() throws IOException {
            if (RXTXPort.this.monThreadisInterrupted) {
                return 0;
            } else {
                ++RXTXPort.this.IOLocked;

                int var2;
                try {
                    int var1 = RXTXPort.this.nativeavailable();
                    var2 = var1;
                } finally {
                    --RXTXPort.this.IOLocked;
                }

                return var2;
            }
        }
    }

    class SerialOutputStream extends OutputStream {
        SerialOutputStream() {
        }

        public void write(int var1) throws IOException {
            if (RXTXPort.this.speed != 0) {
                if (!RXTXPort.this.monThreadisInterrupted) {
                    ++RXTXPort.this.IOLocked;
                    RXTXPort.this.waitForTheNativeCodeSilly();
                    if (RXTXPort.this.fd == 0) {
                        --RXTXPort.this.IOLocked;
                        throw new IOException();
                    } else {
                        try {
                            RXTXPort.this.writeByte(var1, RXTXPort.this.monThreadisInterrupted);
                        } catch (IOException var3) {
                            --RXTXPort.this.IOLocked;
                            throw var3;
                        }

                        --RXTXPort.this.IOLocked;
                    }
                }
            }
        }

        public void write(byte[] var1) throws IOException {
            if (RXTXPort.this.speed != 0) {
                if (!RXTXPort.this.monThreadisInterrupted) {
                    if (RXTXPort.this.fd == 0) {
                        throw new IOException();
                    } else {
                        ++RXTXPort.this.IOLocked;
                        RXTXPort.this.waitForTheNativeCodeSilly();

                        try {
                            RXTXPort.this.writeArray(var1, 0, var1.length, RXTXPort.this.monThreadisInterrupted);
                        } catch (IOException var3) {
                            --RXTXPort.this.IOLocked;
                            throw var3;
                        }

                        --RXTXPort.this.IOLocked;
                    }
                }
            }
        }

        public void write(byte[] var1, int var2, int var3) throws IOException {
            if (RXTXPort.this.speed != 0) {
                if (var2 + var3 > var1.length) {
                    throw new IndexOutOfBoundsException("Invalid offset/length passed to read");
                } else {
                    byte[] var4 = new byte[var3];
                    System.arraycopy(var1, var2, var4, 0, var3);
                    if (RXTXPort.this.fd == 0) {
                        throw new IOException();
                    } else if (!RXTXPort.this.monThreadisInterrupted) {
                        ++RXTXPort.this.IOLocked;
                        RXTXPort.this.waitForTheNativeCodeSilly();

                        try {
                            RXTXPort.this.writeArray(var4, 0, var3, RXTXPort.this.monThreadisInterrupted);
                        } catch (IOException var6) {
                            --RXTXPort.this.IOLocked;
                            throw var6;
                        }

                        --RXTXPort.this.IOLocked;
                    }
                }
            }
        }

        public void flush() throws IOException {
            if (RXTXPort.this.speed != 0) {
                if (RXTXPort.this.fd == 0) {
                    throw new IOException();
                } else if (!RXTXPort.this.monThreadisInterrupted) {
                    ++RXTXPort.this.IOLocked;
                    RXTXPort.this.waitForTheNativeCodeSilly();

                    try {
                        if (RXTXPort.this.nativeDrain(RXTXPort.this.monThreadisInterrupted)) {
                            RXTXPort.this.sendEvent(2, true);
                        }
                    } catch (IOException var2) {
                        --RXTXPort.this.IOLocked;
                        throw var2;
                    }

                    --RXTXPort.this.IOLocked;
                }
            }
        }
    }
}

