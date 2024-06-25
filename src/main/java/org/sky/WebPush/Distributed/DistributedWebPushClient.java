package org.sky.WebPush.Distributed;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.sky.WebPush.General.WebPushClient;
import org.sky.WebPush.General.WebGuard;

//分布式模式下的socket
public class DistributedWebPushClient extends WebPushClient {//端口
    /*private final int port;
    private final String url;
    //为请求添加字段
    private final HttpRequestHandler httpRequestHandler;
    //websocket连接权限判断
    private WebGuard webGuard;

    public DistributedWebPushClient(int port, String url,
                                    HttpRequestHandler httpRequestHandler, WebGuard WebGuard) {
        this.port = port;
        this.url = url;
        this.httpRequestHandler = httpRequestHandler;
        this.webGuard = WebGuard;
    }

    public DistributedWebPushClient(int port, String url, HttpRequestHandler httpRequestHandler) {
        this.port = port;
        this.url = url;
        this.httpRequestHandler = httpRequestHandler;
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
                            p.addLast(new HttpObjectAggregator(65536));
                            p.addLast(httpRequestHandler);
                            p.addLast(new WebSocketServerProtocolHandler(url));
//                            p.addLast(new DistributedSocketHandler(webSocketGuard));
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }*/
}