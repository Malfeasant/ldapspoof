package us.malfeasant.ldapspoof;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

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
            public void completed(AsynchronousSocketChannel connection, Void att) {
                // accept the next connection
                channel.accept(null, this);

                try {
                    Logger.info("Accepted connection from {}", connection.getRemoteAddress());
                    var bb = ByteBuffer.allocate(0x1000);
                    connection.read(bb, null, new CompletionHandler<Integer,Void>() {
                        @Override
                        public void completed(Integer result, Void attachment) {
                            Logger.debug("Received {} bytes.", result);
                            Logger.info("{}", StandardCharsets.UTF_8
                                .decode(bb));   // but this just gets a blank line...
                        }
                        @Override
                        public void failed(Throwable exc, Void attachment) {
                            Logger.error("Problem acceptin response!\n{}", exc);
                        }
                    });
                } catch (IOException e) {
                    // Not sure what would cause this, just log for now.
                    Logger.error("Problem reading channel!\n{}", e);
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                // Not sure what would cause this, just log for now.
                Logger.error("Problem accepting connection!\n{}", exc);
            }
        });
    }
}
