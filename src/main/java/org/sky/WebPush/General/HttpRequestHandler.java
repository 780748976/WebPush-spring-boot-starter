package org.sky.WebPush.General;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.Map;

@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 获取请求的uri
        String uri = request.uri();
        System.out.println("uri:" + uri);
        // 获取请求参数
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        // 获取连接时的userId
        List<String> userIds = parameters.get("userId");
        List<String> tokens = parameters.get("token");
        if (userIds != null && !userIds.isEmpty() && tokens != null && !tokens.isEmpty()) {
            String userId = userIds.get(0);
            String token = tokens.get(0);
            // 将userId和token放入channel的属性中
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
            ctx.channel().attr(AttributeKey.valueOf("token")).set(token);
        }
    }
}
