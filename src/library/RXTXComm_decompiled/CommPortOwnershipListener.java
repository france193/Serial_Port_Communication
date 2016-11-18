package library.RXTXComm_decompiled;

import java.util.EventListener;

public interface CommPortOwnershipListener extends EventListener {
    int PORT_OWNED = 1;
    int PORT_UNOWNED = 2;
    int PORT_OWNERSHIP_REQUESTED = 3;

    void ownershipChange(int var1);
}
