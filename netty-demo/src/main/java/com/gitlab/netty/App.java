package com.gitlab.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;

import java.util.logging.Logger;

public class App {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new SimpleOutHandlerA());
                ch.pipeline().addLast(new SimpleInHandlerA());
                ch.pipeline().addLast(new SimpleInHandlerB());
                ch.pipeline().addLast(new SimpleInHandlerB());
            }
        });

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeInt(1);
        embeddedChannel.writeInbound(buffer);
    }

    static class SimpleInHandlerA extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            logger.info("SimpleInHandlerA.ChannelHandlerContext被回调了");
            ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeBytes("hello world".getBytes());
            ctx.writeAndFlush(buffer);
            super.channelRead(ctx, msg);
        }
    }

    static class SimpleInHandlerB extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            logger.info("SimpleInHandlerB.ChannelHandlerContext被回调了");
            super.channelRead(ctx, msg);
        }
    }

    static class SimpleInHandlerC extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            logger.info("SimpleInHandlerC.ChannelHandlerContext被回调了");
            super.channelRead(ctx, msg);
        }
    }

    static class SimpleOutHandlerA extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if (msg instanceof ByteBuf) {
                ByteBuf slice = ((ByteBuf) msg).slice();
                System.out.println(new String(slice.array()));
            }
            super.write(ctx, msg, promise);
        }


    }
}
