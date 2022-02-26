Netty
IO多路复用 reactor模型

    channel->selector->reactor线程->diapatch(事件)->channelHandler

ChannelInboundHandler：通道入站处理器，底层通道到入站handler方向，向上传递的事件
ChannelOutboundHandler:通道出站处理器，出站处理器向netty通道方向，向下传递的事件

    channelHandler被设计成无状态的，每个通道可以拥有多个channelHandler，通过chanlelPipeline组织，pilelile是一个双向链表，事件在pipiline中的流动，会根据
    入站、出站类型来找到下一个handler来处理
    链表的节点类型是ChannelHandlerContext
    HeadContext:inbound and outbound
    TailContext:only inbound

完整的入站和出站处理流转过程都是通过调用流水线实例的相应入站/出站方法开启的。

入站处理器如何截断？不调用super.channelXXX(ctx)方法 2.不调用ctx.fireXXX
出站处理流程只要开始执行就不能被截断，强行截断会抛出异常，如果业务条件不满足，可以不启动出站处理

可以动态进行热插拔channelPipeline，内部做了synchronize处理

    netty 开发中密切关注缓冲区ByteBuf的释放，如果释放不及时，会造成netty的内存泄露memory leak
    释放缓冲区的方式：
    1.ByteBuf.release()
    2.ReferenceCountUtil.release()
    3.HeadContext,TailContext会自动释放
    
ByteBuf浅层复制的高级使用方式，可以很大程度上避免内存复制，这一点对于大规模消息通信非常重要。

ByteBuf的浅层复制分为两种：切片(slice)浅层复制和整体(duplicate)浅层复制
ByteBuf.slice()
ByteByf.duplicate
都是浅层复制，slice:浅层复制可读端，duplicate整体浅层复制，不会改变媛ByteBuf的引用计数
浅层复制不会去实际复制数据


netty中的零拷贝(应用层中的优化，而不是操作系统层面的零拷贝)
1.大部分场景下，netty接收和发送byteBuf的过程中会直接使用直接内存进行socket通道读写，使用JVM堆内存进行业务处理，会涉及到直接内存
堆内存之间的数据复制，内存的数据复制效率非常低，netty提供了多种方法，帮助应用程序减少内存的复制
1.CompositeByteBuf组合缓冲类，可以将多个ByteBuf合并为一个逻辑上的ByteBuf，避免了各个ByteBuf之间的拷贝
2.浅层复制 slice，duplicate
3.netty文件传输 用到了transferTo
4.将byte数组转换成ByteBuf，提供包装类，避免内存拷贝
5.如果通道接收和发送ByteBuf都使用直接内存进行Socket读写，就不需要进行缓冲区的二次拷贝。如果使用JVM的堆内存进行socket读写，会先将堆内存
拷贝一份到直接内存，相比使用直接内存，这种情况下发送数据会多出一次缓冲区内存的拷贝。所以在发送ByteBuf的时候，尽量使用直接内存而不是jvm堆内存

使用解码器，需要对ByteBuf进行长度检查，有足够的字节才能进行整数的读取，这种长度检查可以使用netty完成，ReplayingDecoder
作用是
1.在读取ByteBuf缓冲区数据之前，需要检查缓冲区释放有足够的字节
2.若缓冲区有足够的字节，则会正常读取，反之则停止解码
他更重要的作用是用于分包传输

底层通信协议是分包传输的，一份数据可能需要多个数据包才能到达对端
