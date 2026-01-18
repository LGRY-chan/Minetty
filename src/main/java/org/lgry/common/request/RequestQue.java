package org.lgry.common.request;

import io.netty.channel.Channel;
import org.lgry.common.Packet;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class RequestQue {
    private final Queue<Packet> pending = new ConcurrentLinkedDeque<>();
    private volatile Channel channel;

    public void send(Packet packet) {
        Channel ch = channel;
        if (ch != null && ch.isActive()) ch.writeAndFlush(packet.encode());
        else pending.add(packet);
    }

    public void active(Channel channel) {
        this.channel = channel;

        Packet packet;
        while ((packet = pending.poll()) != null) {
            channel.write(packet.encode());
        }

        channel.flush();
    }
}
