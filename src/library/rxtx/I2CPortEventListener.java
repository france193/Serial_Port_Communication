package library.rxtx;

import java.util.EventListener;

public interface I2CPortEventListener extends EventListener {
    void I2CEvent(I2CPortEvent var1);
}
