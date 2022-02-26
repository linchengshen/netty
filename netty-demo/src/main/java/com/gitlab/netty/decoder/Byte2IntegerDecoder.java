package com.gitlab.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class Byte2IntegerDecoder extends io.netty.handler.codec.ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 4) {
            int i = in.readInt();
            System.out.println("读取到一个整数:" + i);
            out.add(i);
        }
    }
}
