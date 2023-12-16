package us.malfeasant.ldapspoof;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.tinylog.Logger;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

public class Listen {
    private final ReadOnlyIntegerWrapper portWrapper;
    public final ReadOnlyIntegerProperty portProperty;

    private final AsynchronousServerSocketChannel channel;

    public Listen(int port) throws IOException {
        // Port 0 means auto-select a random port, so we should have some way
        // of getting that back to the UI...
        if (port < 0) throw new IllegalArgumentException("port must not be negative");
        portWrapper = new ReadOnlyIntegerWrapper(port);
        portProperty = portWrapper.getReadOnlyProperty();

        channel = AsynchronousServerSocketChannel.open().bind(
            new InetSocketAddress(port));
        channel.accept(null, new CompletionHandler<AsynchronousSocketChannel,Void>() {
            @Override
            public void completed(AsynchronousSocketChannel ch, Void att) {
                // accept the next connection
                channel.accept(null, this);

                try {
                    Logger.info("Accepted connection from {}", ch.getRemoteAddress());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
            }
        });
    }
}
