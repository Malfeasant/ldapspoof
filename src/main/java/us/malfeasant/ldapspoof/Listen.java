package us.malfeasant.ldapspoof;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

public class Listen {
    private final ReadOnlyIntegerWrapper portWrapper;
    public final ReadOnlyIntegerProperty portProperty;

    public Listen(int port) {
        // Port 0 means auto-select a random port, so we should have some way
        // of getting that back to the UI...
        if (port < 0) throw new IllegalArgumentException("port must not be negative");
        portWrapper = new ReadOnlyIntegerWrapper(port);
        portProperty = portWrapper.getReadOnlyProperty();
    }
}
