package us.malfeasant.ldapspoof;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

public class Listen {
    private final int port;
    
    private final ReadOnlyBooleanWrapper running;
    public final ReadOnlyBooleanProperty isRunning;

    public Listen(int port) {
        if (port < 1) throw new IllegalArgumentException("port must be positive");
        this.port = port;
        running = new ReadOnlyBooleanWrapper(true);
        isRunning = running.getReadOnlyProperty();
    }
}
