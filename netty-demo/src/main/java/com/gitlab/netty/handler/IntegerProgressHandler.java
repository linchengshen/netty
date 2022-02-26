package com.gitlab.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class IntegerProgressHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Integer i = (Integer) msg;
        System.out.println("收到一个整数：" + i);
        super.channelRead(ctx, msg);
    }
}
