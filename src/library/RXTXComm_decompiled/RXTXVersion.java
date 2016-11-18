package library.RXTXComm_decompiled;

public class RXTXVersion {
    private static String Version;

    public RXTXVersion() {
    }

    public static String getVersion() {
        return Version;
    }

    public static native String nativeGetVersion();

    static {
        System.loadLibrary("rxtxSerial");
        Version = "RXTX-2.1-7";
    }
}
