package org.lgry.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lgry.common.encryption.Aes;

import javax.crypto.SecretKey;

public class DecryptHandler extends ChannelInboundHandlerAdapter {

    private final SecretKey key;

    public DecryptHandler(SecretKey key) {
        this.key = key;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        if (!(msg instanceof ByteBuf buf)) {
            ctx.fireChannelRead(msg);
            return;
        }

        byte[] iv = new byte[Aes.IV_LENGTH];
        buf.readBytes(iv);

        byte[] encrypted = new byte[buf.readableBytes()];
        buf.readBytes(encrypted);

        byte[] plain = Aes.decrypt(encrypted, key, iv);

        ctx.fireChannelRead(Unpooled.wrappedBuffer(plain));
    }

}
