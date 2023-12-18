package us.malfeasant.ldapspoof;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

import org.tinylog.Logger;

public class Respond implements CompletionHandler<Integer, Void> {
    private final ByteBuffer buffer;
    private final AsynchronousSocketChannel channel;

    public Respond(AsynchronousSocketChannel channel) {
        this.channel = channel;
        buffer = ByteBuffer.allocate(0x1000);
        try {
            Logger.info("Accepted connection from {}", channel.getRemoteAddress());
        } catch (IOException e) {
            // Not sure what would cause this, just log for now.
            Logger.error("Problem accepting connection!\n{}", e);
        }
        channel.read(buffer, null, this);
    }

    @Override
    public void completed(Integer result, Void attachment) {
        Logger.debug("Received {} bytes.", result);
        buffer.rewind();
        Logger.debug("{}", StandardCharsets.UTF_8.decode(buffer));
        // 0 signifies success right?
        channel.write(ByteBuffer.wrap("0\n".getBytes(StandardCharsets.UTF_8)));
        Logger.debug("Sent response...");
        // read more?
        channel.read(buffer, null, this);
    }

    @Override
    public void failed(Throwable e, Void attachment) {
        Logger.error("Problem accepting response!\n{}", e);
    }
}
