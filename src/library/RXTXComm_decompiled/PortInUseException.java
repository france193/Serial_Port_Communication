package library.RXTXComm_decompiled;

public class PortInUseException extends Exception {
    public String currentOwner;

    PortInUseException(String var1) {
        super(var1);
        this.currentOwner = var1;
    }

    public PortInUseException() {
    }
}
