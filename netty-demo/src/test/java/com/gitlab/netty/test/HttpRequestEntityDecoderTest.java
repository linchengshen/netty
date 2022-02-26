package com.gitlab.netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.util.List;

public class HttpRequestEntityDecoderTest {
    public static void main(String[] args) {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new HttpRequestEntityHandler());
            }
        });

        embeddedChannel.writeInbound(Unpooled.wrappedBuffer("GET /api/v1 http1.1\r\n".getBytes()));
        embeddedChannel.writeInbound(Unpooled.wrappedBuffer("Remote Address: 124.70.100.145:443\r\n".getBytes()));
        embeddedChannel.writeInbound(Unpooled.wrappedBuffer("Referrer Policy: strict-origin-when-cross-origin\r\n".getBytes()));
        embeddedChannel.close();

    }

    static class HttpRequestEntityHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String content = (String) msg;
            System.out.println(content);
        }
    }

    static class StringDecoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            int i = in.readableBytes();
            byte[] buffer = new byte[i];
            in.readBytes(buffer);
            out.add(new String(buffer, "UTF-8"));
        }
    }
}
