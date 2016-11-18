package library.RXTXComm_decompiled;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

final class LPRPort extends ParallelPort {
    private static final boolean debug = false;
    private int fd;
    private final LPRPort.ParallelOutputStream out = new LPRPort.ParallelOutputStream();
    private final LPRPort.ParallelInputStream in = new LPRPort.ParallelInputStream();
    private int lprmode = 0;
    private int timeout = 0;
    private int threshold = 1;
    private ParallelPortEventListener PPEventListener;
    private LPRPort.MonitorThread monThread;

    private static native void Initialize();

    public LPRPort(String var1) throws PortInUseException {
        this.fd = this.open(var1);
        this.name = var1;
    }

    private synchronized native int open(String var1) throws PortInUseException;

    public OutputStream getOutputStream() {
        return this.out;
    }

    public InputStream getInputStream() {
        return this.in;
    }

    public int getMode() {
        return this.lprmode;
    }

    public int setMode(int var1) throws UnsupportedCommOperationException {
        try {
            this.setLPRMode(var1);
        } catch (UnsupportedCommOperationException var3) {
            var3.printStackTrace();
            return -1;
        }

        this.lprmode = var1;
        return 0;
    }

    public void restart() {
        System.out.println("restart() is not implemented");
    }

    public void suspend() {
        System.out.println("suspend() is not implemented");
    }

    public native boolean setLPRMode(int var1) throws UnsupportedCommOperationException;

    public native boolean isPaperOut();

    public native boolean isPrinterBusy();

    public native boolean isPrinterError();

    public native boolean isPrinterSelected();

    public native boolean isPrinterTimedOut();

    private native void nativeClose();

    public synchronized void close() {
        if (this.fd >= 0) {
            this.nativeClose();
            super.close();
            this.removeEventListener();
            this.fd = 0;
            Runtime.getRuntime().gc();
        }
    }

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

    public void enableReceiveTimeout(int var1) {
        if (var1 > 0) {
            this.timeout = var1;
        } else {
            this.timeout = 0;
        }

    }

    public void disableReceiveTimeout() {
        this.timeout = 0;
    }

    public boolean isReceiveTimeoutEnabled() {
        return this.timeout > 0;
    }

    public int getReceiveTimeout() {
        return this.timeout;
    }

    public void enableReceiveThreshold(int var1) {
        if (var1 > 1) {
            this.threshold = var1;
        } else {
            this.threshold = 1;
        }

    }

    public void disableReceiveThreshold() {
        this.threshold = 1;
    }

    public int getReceiveThreshold() {
        return this.threshold;
    }

    public boolean isReceiveThresholdEnabled() {
        return this.threshold > 1;
    }

    public native void setInputBufferSize(int var1);

    public native int getInputBufferSize();

    public native void setOutputBufferSize(int var1);

    public native int getOutputBufferSize();

    public native int getOutputBufferFree();

    protected native void writeByte(int var1) throws IOException;

    protected native void writeArray(byte[] var1, int var2, int var3) throws IOException;

    protected native void drain() throws IOException;

    protected native int nativeavailable() throws IOException;

    protected native int readByte() throws IOException;

    protected native int readArray(byte[] var1, int var2, int var3) throws IOException;

    native void eventLoop();

    public boolean checkMonitorThread() {
        return this.monThread != null ? this.monThread.isInterrupted() : true;
    }

    public synchronized boolean sendEvent(int var1, boolean var2) {
        if (this.fd != 0 && this.PPEventListener != null && this.monThread != null) {
            switch (var1) {
                case 1:
                    if (!this.monThread.monError) {
                        return false;
                    }
                    break;
                case 2:
                    if (!this.monThread.monBuffer) {
                        return false;
                    }
                    break;
                default:
                    System.err.println("unknown event:" + var1);
                    return false;
            }

            ParallelPortEvent var3 = new ParallelPortEvent(this, var1, !var2, var2);
            if (this.PPEventListener != null) {
                this.PPEventListener.parallelEvent(var3);
            }

            if (this.fd != 0 && this.PPEventListener != null && this.monThread != null) {
                try {
                    Thread.sleep(50L);
                } catch (Exception var5) {
                    ;
                }

                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public synchronized void addEventListener(ParallelPortEventListener var1) throws TooManyListenersException {
        if (this.PPEventListener != null) {
            throw new TooManyListenersException();
        } else {
            this.PPEventListener = var1;
            this.monThread = new LPRPort.MonitorThread();
            this.monThread.start();
        }
    }

    public synchronized void removeEventListener() {
        this.PPEventListener = null;
        if (this.monThread != null) {
            this.monThread.interrupt();
            this.monThread = null;
        }

    }

    public synchronized void notifyOnError(boolean var1) {
        System.out.println("notifyOnError is not implemented yet");
        this.monThread.monError = var1;
    }

    public synchronized void notifyOnBuffer(boolean var1) {
        System.out.println("notifyOnBuffer is not implemented yet");
        this.monThread.monBuffer = var1;
    }

    protected void finalize() {
        if (this.fd > 0) {
            this.close();
        }

    }

    static {
        System.loadLibrary("rxtxParallel");
        Initialize();
    }

    class MonitorThread extends Thread {
        private boolean monError = false;
        private boolean monBuffer = false;

        MonitorThread() {
        }

        public void run() {
            LPRPort.this.eventLoop();
            yield();
        }
    }

    class ParallelInputStream extends InputStream {
        ParallelInputStream() {
        }

        public int read() throws IOException {
            if (LPRPort.this.fd == 0) {
                throw new IOException();
            } else {
                return LPRPort.this.readByte();
            }
        }

        public int read(byte[] var1) throws IOException {
            if (LPRPort.this.fd == 0) {
                throw new IOException();
            } else {
                return LPRPort.this.readArray(var1, 0, var1.length);
            }
        }

        public int read(byte[] var1, int var2, int var3) throws IOException {
            if (LPRPort.this.fd == 0) {
                throw new IOException();
            } else {
                return LPRPort.this.readArray(var1, var2, var3);
            }
        }

        public int available() throws IOException {
            if (LPRPort.this.fd == 0) {
                throw new IOException();
            } else {
                return LPRPort.this.nativeavailable();
            }
        }
    }

    class ParallelOutputStream extends OutputStream {
        ParallelOutputStream() {
        }

        public synchronized void write(int var1) throws IOException {
            if (LPRPort.this.fd == 0) {
                throw new IOException();
            } else {
                LPRPort.this.writeByte(var1);
            }
        }

        public synchronized void write(byte[] var1) throws IOException {
            if (LPRPort.this.fd == 0) {
                throw new IOException();
            } else {
                LPRPort.this.writeArray(var1, 0, var1.length);
            }
        }

        public synchronized void write(byte[] var1, int var2, int var3) throws IOException {
            if (LPRPort.this.fd == 0) {
                throw new IOException();
            } else {
                LPRPort.this.writeArray(var1, var2, var3);
            }
        }

        public synchronized void flush() throws IOException {
            if (LPRPort.this.fd == 0) {
                throw new IOException();
            }
        }
    }
}
