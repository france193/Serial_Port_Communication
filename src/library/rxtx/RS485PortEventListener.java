package library.rxtx;

import java.util.EventListener;

public interface RS485PortEventListener extends EventListener {
    void RS485Event(RS485PortEvent var1);
}

