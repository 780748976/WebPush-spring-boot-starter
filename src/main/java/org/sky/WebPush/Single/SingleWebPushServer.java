package org.sky.WebPush.Single;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.sky.WebPush.General.WebPushClient;

//单机模式下的socket
@ChannelHandler.Sharable
public class SingleWebPushServer extends WebPushClient {
    //端口
    private final int port;
    private final String url;
    //为请求添加字段
    private final SingleSocketHandler singleSocketHandler;


    public SingleWebPushServer(int port, String url,
                               SingleSocketHandler singleSocketHandler) {
        this.port = port;
        this.url = url;
        this.singleSocketHandler = singleSocketHandler;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)  {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpServerCodec());
                            p.addLast(new ChunkedWriteHandler());
                            p.addLast(new HttpObjectAggregator(65536));
                            p.addLast(new IdleStateHandler(0,0, 120));
                            p.addLast(singleSocketHandler);
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
