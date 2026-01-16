package org.lgry.common.handler;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class ChannelInitializer extends io.netty.channel.ChannelInitializer<SocketChannel> {

    private Handler handler = new Handler();

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

        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(handler);
    }

    public ChannelInitializer withHandler(Handler handler) {
        this.handler = handler;
        return this;
    }
}
