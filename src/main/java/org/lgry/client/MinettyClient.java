package org.lgry.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.lgry.common.Packet;
import org.lgry.common.handler.ChannelInitializer;

public class MinettyClient {
    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();

        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer());
            Channel channel = bootstrap
                    .connect("127.0.0.1", 8080)
                    .sync()
                    .channel();

            channel.writeAndFlush(new Packet("HELLO_WORLD").with("you", "gay").encode());

            channel.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            group.shutdownGracefully();
        }
    }
}
