package library.rxtx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

final class I2C extends I2CPort {
    private int fd;
    static boolean dsrFlag;
    private final I2C.I2COutputStream out = new I2C.I2COutputStream();
    private final I2C.I2CInputStream in = new I2C.I2CInputStream();
    private int speed = 9600;
    private int dataBits = 8;
    private int stopBits = 1;
    private int parity = 0;
    private int flowmode = 0;
    private int timeout = 0;
    private int threshold = 0;
    private int InputBuffer = 0;
    private int OutputBuffer = 0;
    private I2CPortEventListener SPEventListener;
    private I2C.MonitorThread monThread;
    private int dataAvailable = 0;

    private static native void Initialize();

    public I2C(String var1) throws PortInUseException {
        this.fd = this.open(var1);
    }

    private native int open(String var1) throws PortInUseException;

    public OutputStream getOutputStream() {
        return this.out;
    }

    public InputStream getInputStream() {
        return this.in;
    }

    public void setI2CPortParams(int var1, int var2, int var3, int var4) throws UnsupportedCommOperationException {
        this.nativeSetI2CPortParams(var1, var2, var3, var4);
        this.speed = var1;
        this.dataBits = var2;
        this.stopBits = var3;
        this.parity = var4;
    }

    private native void nativeSetI2CPortParams(int var1, int var2, int var3, int var4) throws UnsupportedCommOperationException;

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
        try {
            this.setflowcontrol(var1);
        } catch (IOException var3) {
            var3.printStackTrace();
            return;
        }

        this.flowmode = var1;
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

    public native boolean NativeisReceiveTimeoutEnabled();

    public native void NativeEnableReceiveTimeoutThreshold(int var1, int var2, int var3);

    public void disableReceiveTimeout() {
        this.enableReceiveTimeout(0);
    }

    public void enableReceiveTimeout(int var1) {
        if (var1 >= 0) {
            this.timeout = var1;
            this.NativeEnableReceiveTimeoutThreshold(var1, this.threshold, this.InputBuffer);
        } else {
            System.out.println("Invalid timeout");
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
            System.out.println("Invalid Threshold");
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
        this.InputBuffer = var1;
    }

    public int getInputBufferSize() {
        return this.InputBuffer;
    }

    public void setOutputBufferSize(int var1) {
        this.OutputBuffer = var1;
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

    private native void writeByte(int var1) throws IOException;

    private native void writeArray(byte[] var1, int var2, int var3) throws IOException;

    private native void drain() throws IOException;

    private native int nativeavailable() throws IOException;

    private native int readByte() throws IOException;

    private native int readArray(byte[] var1, int var2, int var3) throws IOException;

    native void eventLoop();

    public void sendEvent(int var1, boolean var2) {
        switch (var1) {
            case 1:
                this.dataAvailable = 1;
                if (!this.monThread.Data) {
                    return;
                }
                break;
            case 2:
                if (!this.monThread.Output) {
                    return;
                }
                break;
            case 3:
                if (!this.monThread.CTS) {
                    return;
                }
                break;
            case 4:
                if (!this.monThread.DSR) {
                    return;
                }
                break;
            case 5:
                if (!this.monThread.RI) {
                    return;
                }
                break;
            case 6:
                if (!this.monThread.CD) {
                    return;
                }
                break;
            case 7:
                if (!this.monThread.OE) {
                    return;
                }
                break;
            case 8:
                if (!this.monThread.PE) {
                    return;
                }
                break;
            case 9:
                if (!this.monThread.FE) {
                    return;
                }
                break;
            case 10:
                if (!this.monThread.BI) {
                    return;
                }
                break;
            default:
                System.err.println("unknown event:" + var1);
                return;
        }

        I2CPortEvent var3 = new I2CPortEvent(this, var1, !var2, var2);
        if (this.SPEventListener != null) {
            this.SPEventListener.I2CEvent(var3);
        }

    }

    public void addEventListener(I2CPortEventListener var1) throws TooManyListenersException {
        if (this.SPEventListener != null) {
            throw new TooManyListenersException();
        } else {
            this.SPEventListener = var1;
            this.monThread = new I2C.MonitorThread();
            this.monThread.start();
        }
    }

    public void removeEventListener() {
        this.SPEventListener = null;
        if (this.monThread != null) {
            this.monThread.interrupt();
            this.monThread = null;
        }

    }

    public void notifyOnDataAvailable(boolean var1) {
        this.monThread.Data = var1;
    }

    public void notifyOnOutputEmpty(boolean var1) {
        this.monThread.Output = var1;
    }

    public void notifyOnCTS(boolean var1) {
        this.monThread.CTS = var1;
    }

    public void notifyOnDSR(boolean var1) {
        this.monThread.DSR = var1;
    }

    public void notifyOnRingIndicator(boolean var1) {
        this.monThread.RI = var1;
    }

    public void notifyOnCarrierDetect(boolean var1) {
        this.monThread.CD = var1;
    }

    public void notifyOnOverrunError(boolean var1) {
        this.monThread.OE = var1;
    }

    public void notifyOnParityError(boolean var1) {
        this.monThread.PE = var1;
    }

    public void notifyOnFramingError(boolean var1) {
        this.monThread.FE = var1;
    }

    public void notifyOnBreakInterrupt(boolean var1) {
        this.monThread.BI = var1;
    }

    private native void nativeClose();

    public void close() {
        this.setDTR(false);
        this.setDSR(false);
        this.nativeClose();
        super.close();
        this.fd = 0;
    }

    protected void finalize() {
        if (this.fd > 0) {
            this.close();
        }

    }

    static {
        System.loadLibrary("rxtxI2C");
        Initialize();
        dsrFlag = false;
    }

    class MonitorThread extends Thread {
        private boolean CTS = false;
        private boolean DSR = false;
        private boolean RI = false;
        private boolean CD = false;
        private boolean OE = false;
        private boolean PE = false;
        private boolean FE = false;
        private boolean BI = false;
        private boolean Data = false;
        private boolean Output = false;

        MonitorThread() {
        }

        public void run() {
            I2C.this.eventLoop();
        }
    }

    class I2CInputStream extends InputStream {
        I2CInputStream() {
        }

        public int read() throws IOException {
            I2C.this.dataAvailable = 0;
            return I2C.this.readByte();
        }

        public int read(byte[] var1) throws IOException {
            return this.read(var1, 0, var1.length);
        }

        public int read(byte[] var1, int var2, int var3) throws IOException {
            I2C.this.dataAvailable = 0;
            int var4 = 0;
            boolean var5 = false;

            int[] var6;
            for (var6 = new int[]{var1.length, I2C.this.InputBuffer, var3}; var6[var4] == 0 && var4 < var6.length; ++var4) {
                ;
            }

            int var9;
            for (var9 = var6[var4]; var4 < var6.length; ++var4) {
                if (var6[var4] > 0) {
                    var9 = Math.min(var9, var6[var4]);
                }
            }

            var9 = Math.min(var9, I2C.this.threshold);
            if (var9 == 0) {
                var9 = 1;
            }

            int var7 = this.available();
            int var8 = I2C.this.readArray(var1, var2, var9);
            return var8;
        }

        public int available() throws IOException {
            return I2C.this.nativeavailable();
        }
    }

    class I2COutputStream extends OutputStream {
        I2COutputStream() {
        }

        public void write(int var1) throws IOException {
            I2C.this.writeByte(var1);
        }

        public void write(byte[] var1) throws IOException {
            I2C.this.writeArray(var1, 0, var1.length);
        }

        public void write(byte[] var1, int var2, int var3) throws IOException {
            I2C.this.writeArray(var1, var2, var3);
        }

        public void flush() throws IOException {
            I2C.this.drain();
        }
    }
}
