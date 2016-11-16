package library.rxtx;

import java.util.EventObject;

public class ParallelPortEvent extends EventObject {
    public static final int PAR_EV_ERROR = 1;
    public static final int PAR_EV_BUFFER = 2;
    private boolean OldValue;
    private boolean NewValue;
    private int eventType;

    public ParallelPortEvent(ParallelPort var1, int var2, boolean var3, boolean var4) {
        super(var1);
        this.OldValue = var3;
        this.NewValue = var4;
        this.eventType = var2;
    }

    public int getEventType() {
        return this.eventType;
    }

    public boolean getNewValue() {
        return this.NewValue;
    }

    public boolean getOldValue() {
        return this.OldValue;
    }
}
