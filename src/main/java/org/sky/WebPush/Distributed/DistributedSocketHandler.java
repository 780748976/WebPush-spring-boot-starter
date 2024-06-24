package org.sky.WebPush.Distributed;/*
package org.sky.PushSocket.Distributed;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import org.sky.PushSocket.General.WebSocketGuard;
import org.sky.PushSocket.Utils.RedisUtil;

import javax.annotation.Resource;
import java.util.Map;

public class DistributedSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final WebSocketGuard webSocketGuard;
    private final Gson gson;
    private final String redisHashName;
    @Resource
    RedisUtil redisUtil;

    public DistributedSocketHandler(String redisHashName,WebSocketGuard webSocketGuard) {
        this.redisHashName = redisHashName;
        this.webSocketGuard = webSocketGuard;
        this.gson = new Gson();
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        // 获取连接时的userId
        String userId = (String) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        // 处理消息
        MessageProcessed(userId,msg.text());
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 获取连接时的userId
        String userId = (String) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        // 获取连接时的token
        String token = (String) ctx.channel().attr(AttributeKey.valueOf("token")).get();
        if (!webSocketGuard.hasPermission(userId,token)) {
            ctx.close();
            return;
        }
        // 添加连接
        String jsonCtx = gson.toJson(ctx);
        redisUtil.hPut(redisHashName,userId,jsonCtx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        // 获取连接时的userId
        String userId = (String) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        // 断开连接
        redisUtil.hDelete(redisHashName,userId);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 异常处理
        cause.printStackTrace();
        ctx.close();
    }

    //信息处理
    public void MessageProcessed(String userId,Object msg) {
    }

    //推送消息
    public void pushMessage(String userId, Object message) {
        ChannelHandlerContext ctx = gson.fromJson(redisUtil.get(userId), ChannelHandlerContext.class);
        if (ctx != null) {
            String json = gson.toJson(message);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    //向所有用户推送信息
    public void pushAllMessage(String message) {
        String json = gson.toJson(message);
        for (ChannelHandlerContext ctx : map.values()) {
            ctx.channel().writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    //获取当前连接数
    public int getConnectNum() {
        return map.size();
    }

    //断开连接
    public void closeConnect(String userId) {
        ChannelHandlerContext ctx = map.get(userId);
        if (ctx != null) {
            ctx.close();
        }
    }

    //断开所有连接
    public void closeAllConnect() {
        for (ChannelHandlerContext ctx : map.values()) {
            ctx.close();
        }
    }

    //获取所有连接
    public Map<String, ChannelHandlerContext> getAllConnect() {
        return map;
    }

    //获取指定连接
    public ChannelHandlerContext getConnect(String userId) {
        return map.get(userId);
    }
}
*/
