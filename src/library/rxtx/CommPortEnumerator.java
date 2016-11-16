package library.rxtx;

import java.util.Enumeration;

class CommPortEnumerator implements Enumeration {
    private CommPortIdentifier index;
    private static final boolean debug = false;

    CommPortEnumerator() {
    }

    public Object nextElement() {
        Object var1 = CommPortIdentifier.Sync;
        synchronized(CommPortIdentifier.Sync) {
            if(this.index != null) {
                this.index = this.index.next;
            } else {
                this.index = CommPortIdentifier.CommPortIndex;
            }

            return this.index;
        }
    }

    public boolean hasMoreElements() {
        Object var1 = CommPortIdentifier.Sync;
        synchronized(CommPortIdentifier.Sync) {
            return this.index != null?this.index.next != null: CommPortIdentifier.CommPortIndex != null;
        }
    }
}

