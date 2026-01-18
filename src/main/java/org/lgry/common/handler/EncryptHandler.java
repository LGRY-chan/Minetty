package org.lgry.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.lgry.common.encryption.Aes;

import javax.crypto.SecretKey;

public class EncryptHandler extends ChannelOutboundHandlerAdapter {

    private final SecretKey key;

    public EncryptHandler(SecretKey key) {
        this.key = key;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
            throws Exception {

        if (!(msg instanceof ByteBuf buf)) {
            ctx.write(msg, promise);
            return;
        }

        byte[] plain = new byte[buf.readableBytes()];
        buf.readBytes(plain);

        byte[] iv = Aes.newIv();
        byte[] encrypted = Aes.encrypt(plain, key, iv);

        ByteBuf out = Unpooled.buffer(iv.length + encrypted.length);
        out.writeBytes(iv);         // [IV]
        out.writeBytes(encrypted);  // [CipherText + TAG]

        ctx.write(out, promise);
    }

}
