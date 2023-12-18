package us.malfeasant.ldapspoof;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.tinylog.Logger;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

public class Listen implements CompletionHandler<AsynchronousSocketChannel, Void> {
    private final ReadOnlyIntegerWrapper portWrapper;
    public final ReadOnlyIntegerProperty portProperty;

    private final AsynchronousServerSocketChannel channel;

    public Listen(int port) throws IOException {
        if (port < 0) throw new IllegalArgumentException("port must not be negative");
        portWrapper = new ReadOnlyIntegerWrapper(port);
        portProperty = portWrapper.getReadOnlyProperty();

        channel = AsynchronousServerSocketChannel.open().bind(
            new InetSocketAddress(port)
        );
        // Port 0 means auto-select a port, so this is how we get it back to the UI...
        // Interesting to note, this is a different InetSocketAddress than the one we
        // passed in, we can't just read it back from the same object.
        var actualPort = ((InetSocketAddress)channel.getLocalAddress()).getPort();
        portWrapper.set(actualPort);
        Logger.info("Listening on port {}", actualPort);
        channel.accept(null, this);
    }

    @Override
    public void completed(AsynchronousSocketChannel connection, Void att) {
        // accept the next connection
        channel.accept(null, this);

        new Respond(connection);
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        // Not sure what would cause this, just log for now.
        Logger.error("Problem accepting connection!\n{}", exc);
    }
}
