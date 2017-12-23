package net.ddns.kennhuang.christmastree;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * Created by user on 12/15/2017.
 */

public class Channel {

    private Pipe pipe;
    private Pipe.SourceChannel sourceChannel;
    private Pipe.SinkChannel sinkChannel;


    public Channel() throws IOException {
        pipe = Pipe.open();
        sourceChannel = pipe.source();
        sinkChannel = pipe.sink();
    }

    public void Notify(String str) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(48);
        buf.clear();
        buf.put(str.getBytes());
        buf.flip();

        while (buf.hasRemaining()){
            sinkChannel.write(buf);
        }
    }

    public Pipe.SourceChannel Listen() {
        return sourceChannel;
    }
}
