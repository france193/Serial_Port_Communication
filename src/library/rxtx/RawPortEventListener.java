package library.rxtx;

import java.util.EventListener;

public interface RawPortEventListener extends EventListener {
    void RawEvent(RawPortEvent var1);
}
