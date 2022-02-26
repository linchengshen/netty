package com.gitlab.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyEchoServer {

    public static void main(String[] args) {
        new NettyEchoServer().startServer(11111);
    }

    public void startServer(int port) {
        // 使用ServerBootStrap进行netty组件装配
        // 父Channel，子Channel，自动到Handler
        // reactor（通常会分离。通道建立和通道的数据传输处理分开)
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 设置父通道类型
        serverBootstrap.channel(NioServerSocketChannel.class);

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        serverBootstrap.group(bossGroup, workerGroup);

        // 设置父通道配置
        // serverBootstrap.option();

        // 设置子通道配置
        // serverBootstrap.childOption();

        // 设置自动到初始化器，有新通道建立时，会调用一次，将业务Handler加入到PipeLine中
        // 子通道类型要和父通道配套使用
        // 父通道handler初始化器 一般不需要设置
        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                // Handler设计成无状态的，这里甚至可以复用
                ch.pipeline().addLast(NettyEchoHandler.INSTANCE);
            }
        });
        // 绑定端口
        try {
            // Start the server.
            ChannelFuture f = serverBootstrap.bind(port).sync();
            System.out.println("server is listening at port:" + port);
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
