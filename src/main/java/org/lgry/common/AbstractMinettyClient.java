package org.lgry.common;

import com.google.gson.JsonObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.lgry.Main;
import org.lgry.common.handler.ChannelInitializer;
import org.lgry.common.handler.MainHandler;
import org.lgry.common.request.RequestMethod;
import org.lgry.common.request.RequestQue;
import org.lgry.common.util.IPUtil;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractMinettyClient {

    private int port;
    private int serverPort;
    private String fullIP;
    private String secret;
    protected Channel mainChannel;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private MainHandler handler = new MainHandler();
    private RequestQue requestQue = new RequestQue();

    public abstract int getPort();
    public abstract int getServerPort();
    public abstract String getSecret();

    private String getFullIP() {
        int port = getPort();
        String ip = IPUtil.getPublicIP();
        if (ip == null || ip.isEmpty() || port == 0) return null;
        return IPUtil.getPublicIP() + ":" + String.valueOf(getPort());
    }

    public int MGetPort() {
        if (this.port == 0) this.port = getPort();
        return this.port;
    }

    public int MGetServerPort() {
        if (this.serverPort == 0) this.serverPort = getServerPort();
        return this.serverPort;
    }

    public String MGetSecret() {
        if (this.secret == null) this.secret = getSecret();
        return this.secret;
    }

    public String MGetFullIP() {
        if (this.fullIP == null) this.fullIP = getFullIP();
        return this.fullIP;
    }

    private Packet getHandShakePacket() {
        String IP = MGetFullIP();
        if (IP == null) return null;
        return new Packet(RequestMethod.HANDSHAKE).with("ip", IP).with("time", Instant.now().toString());
    }

    private void start() {

        EventLoopGroup group = new NioEventLoopGroup();

        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer().withHandler(handler));
            mainChannel = bootstrap
                    .connect("127.0.0.1", MGetServerPort()) // I Just changed inetHost to localhost since my internet was fucked.
                    .sync()                                         // WARNING: You should get it back ok?
                    .channel();

            requestQue.active(mainChannel);
            mainChannel.writeAndFlush(getHandShakePacket().encode());
            /*executorService.submit(() -> {
                try {
                    mainChannel.closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });*/
            mainChannel.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            group.shutdownGracefully();
        }
    }

    public void startAsync() {
        new Thread(() -> {
            start();
        }).start();
    }

    public CompletableFuture<JsonObject> send(Packet packet) throws RuntimeException {
        if (packet.requestMethod != RequestMethod.GET) throw new RuntimeException();

        CompletableFuture<JsonObject> future = handler.getPRManager().register(packet);
        requestQue.send(packet); // Send later
        return future;
    }

}
