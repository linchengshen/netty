package com.gitlab.netty.test;

import com.gitlab.netty.decoder.Byte2IntegerDecoder;
import com.gitlab.netty.decoder.ReplayingIntegerDecoder;
import com.gitlab.netty.handler.IntegerProgressHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;

public class Byte2IntegerDecoderTest {
    public static void main(String[] args) {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                // ch.pipeline().addLast(new Byte2IntegerDecoder());
                ch.pipeline().addLast(new ReplayingIntegerDecoder());
                ch.pipeline().addLast(new IntegerProgressHandler());
            }
        });

        for (int i = 0; i < 100; i++) {
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeInt(i);
            embeddedChannel.writeInbound(buffer);
        }
        embeddedChannel.close();
    }
}
