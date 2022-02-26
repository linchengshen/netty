package com.gitlab.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class ReplayingIntegerDecoder extends ReplayingDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int i = in.readInt();
        System.out.println("收到一个整数：" + i);
    }
}
