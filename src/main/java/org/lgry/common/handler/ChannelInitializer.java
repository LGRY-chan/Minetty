package org.lgry.common.handler;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import javax.crypto.SecretKey;

public class ChannelInitializer extends io.netty.channel.ChannelInitializer<SocketChannel> {

    private MainHandler handler = new MainHandler();
    private SecretKey secretKey;

    @Override
    protected void initChannel(SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(
                1024*1024,
                0,
                4,
                0,
                4
        ));
        //pipeline.addLast(new DecryptHandler(secretKey));
        pipeline.addLast(new LengthFieldPrepender(4));
        //pipeline.addLast(new EncryptHandler(secretKey));
        pipeline.addLast(handler);
    }

    public ChannelInitializer withHandler(MainHandler handler) {
        this.handler = handler;
        return this;
    }
}
