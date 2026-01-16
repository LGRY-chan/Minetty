package org.lgry.server;

import com.google.gson.JsonObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.lgry.common.handler.Handler;
import org.lgry.common.handler.ChannelInitializer;

import java.util.function.Function;

public class MinettyServer {
    private final Handler handler = new Handler();

    public MinettyServer() {
    }

    public void start() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 서버 부트스트랩
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer().withHandler(this.handler));


            ChannelFuture future = bootstrap.bind(8080).sync();



            // 서버 종료까지 대기
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void register(String key, Function<JsonObject, JsonObject> callback) {
        this.handler.getRCManager().register(key, callback);
    }
}
