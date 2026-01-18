package org.lgry.common.handler;

import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.lgry.Main;
import org.lgry.common.Packet;
import org.lgry.common.request.PendingRequestManager;
import org.lgry.common.request.RequestCallbackManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@ChannelHandler.Sharable
public class MainHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final PendingRequestManager PRManager;
    private final RequestCallbackManager RCManager;
    private Function<JsonObject, JsonObject> HANDSHAKE_CALLBACK;
    private final ExecutorService WORKER_POOL = Executors.newFixedThreadPool(4);

    public MainHandler(PendingRequestManager PRManager, RequestCallbackManager RCManager) {
        this.PRManager = PRManager;
        this.RCManager = RCManager;
    }

    public MainHandler() {
        this.PRManager = new PendingRequestManager();
        this.RCManager = new RequestCallbackManager();
    }

    public PendingRequestManager getPRManager() {
        return this.PRManager;
    }
    public RequestCallbackManager getRCManager() {
        return this.RCManager;
    }
    public void setHandshakeCallback(Function<JsonObject, JsonObject> callback) {
        this.HANDSHAKE_CALLBACK = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, ByteBuf msg) {
        Packet packet = new Packet(msg);

        if (packet.content.isEmpty() || packet.uuid.isEmpty()) return;

        Main.logger.info(packet.toString());

        switch (packet.requestMethod) {

            case GET -> {

                Function<JsonObject, JsonObject> callback = RCManager.get(packet.getKey());

                if (!(callback == null)) {

                    // run async
                    CompletableFuture<JsonObject> future = CompletableFuture.supplyAsync(() -> {
                        return callback.apply(packet.content);
                    }, WORKER_POOL);

                    // complete task
                    future.thenAccept(r -> {
                        future.complete(r);
                        Packet responsePacket = packet.getResponsePacket(r);
                        context.executor().execute(() -> {
                            context.writeAndFlush(responsePacket.encode());
                        });
                    });

                } else {
                    context.executor().execute(() -> {
                        context.writeAndFlush(
                                packet.getResponsePacket(new JsonObject())
                                        .with("RESPONSE", 404).encode());
                    });
                }
            }
            case RESPONSE -> this.PRManager.complete(packet.uuid, packet.content);
            case HANDSHAKE -> {

                // run async
                CompletableFuture<JsonObject> future = CompletableFuture.supplyAsync(() -> {
                    return HANDSHAKE_CALLBACK.apply(packet.content);
                }, WORKER_POOL);

                // complete task
                future.thenAccept(r -> {
                    future.complete(r);
                    Packet responsePacket = packet.getResponsePacket(r);
                    context.executor().execute(() -> {
                        context.writeAndFlush(responsePacket.encode());
                    });
                });

            }

        }
    }
}
