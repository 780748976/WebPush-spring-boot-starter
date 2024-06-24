package org.sky.WebPush.Service.Impl;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.sky.WebPush.Global.ChannelGlobal;
import org.sky.WebPush.Service.PushService;

public class PushServiceImpl implements PushService {

    private final Gson gson = new Gson();
    //推送消息
    public void pushMessage(String userId, Object message) {
        Channel channel = ChannelGlobal.getUserChannelMap().get(userId);
        if (channel != null) {
            String json = gson.toJson(message);
            channel.writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    //向所有用户推送信息
    public void pushAllMessage(String message) {
        String json = gson.toJson(message);
        for (Channel channel : ChannelGlobal.getUserChannelMap().values()) {
            channel.writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    //获取当前连接数
    public int getConnectNum() {
        return ChannelGlobal.getChannelGroup().size();
    }
}
