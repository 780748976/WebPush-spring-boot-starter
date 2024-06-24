package org.sky.WebPush.Single;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.sky.WebPush.General.WebSocketGuard;
import org.sky.WebPush.Global.ChannelGlobal;

import java.util.Date;

public class ExampleHandler extends SingleSocketHandler {
    public ExampleHandler(String url, int port, WebSocketGuard webSocketGuard) {
        super(url, port, webSocketGuard);
    }
    public ExampleHandler(WebSocketGuard webSocketGuard) {
        super(webSocketGuard);
    }
    public ExampleHandler() {
        super();
    }
    @Override
    public void MessageProcessed(String userId, Object msg) {
        TextWebSocketFrame tws = new TextWebSocketFrame(userId + "：" + msg + "，现在时刻：" + new Date().toString());
        // 群发
        ChannelGlobal.getChannelGroup().writeAndFlush(tws);
    }
}
