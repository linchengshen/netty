package com.gitlab.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

// io.netty.channel.ChannelHandlerAdapter.isSharable
// 标记此Handler的实例可被多个channel共享,加入到多个pipeLine中
@ChannelHandler.Sharable
public class NettyEchoHandler extends ChannelInboundHandlerAdapter {

    public static final NettyEchoHandler INSTANCE = new NettyEchoHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, final Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("msg type: " + (byteBuf.hasArray() ? "堆内存" : "直接内存"));
        int i = byteBuf.readableBytes();
        byte[] buffer = new byte[i];
        byteBuf.getBytes(0, buffer);
        System.out.println("server received: " + new String(buffer, "UTF-8"));
        System.out.println("写回前：" + byteBuf.refCnt());
        // 写回数据 异步任务
        ChannelFuture f = ctx.writeAndFlush(msg);
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("写回后：" + ((ByteBuf) msg).refCnt());

            }
        });
        super.channelRead(ctx, msg);
    }
}
