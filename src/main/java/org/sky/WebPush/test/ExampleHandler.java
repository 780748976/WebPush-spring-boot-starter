package org.sky.WebPush.test;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.sky.WebPush.General.WebGuard;
import org.sky.WebPush.Global.ChannelGlobal;
import org.sky.WebPush.Single.SingleSocketHandler;

import java.util.Date;

public class ExampleHandler extends SingleSocketHandler {
    public ExampleHandler(String url, int port, WebGuard webGuard) {
        super(url, port, webGuard);
    }
    public ExampleHandler(WebGuard webGuard) {
        super(webGuard);
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
