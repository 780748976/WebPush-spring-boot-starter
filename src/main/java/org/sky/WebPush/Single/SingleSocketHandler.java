package org.sky.WebPush.Single;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.sky.WebPush.Config.WebPushProperties;
import org.sky.WebPush.General.WebSocketGuard;
import org.sky.WebPush.Global.ChannelGlobal;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

@ChannelHandler.Sharable
public class SingleSocketHandler extends SimpleChannelInboundHandler<Object> {

    private final WebSocketGuard webSocketGuard;
    private WebSocketServerHandshaker handShaker;
    private final String url;
    private final int port;

    public SingleSocketHandler(String url, int port, WebSocketGuard webSocketGuard) {
        this.url = url;
        this.port = port;
        this.webSocketGuard = webSocketGuard;
    }

    public SingleSocketHandler(WebSocketGuard webSocketGuard) {
        this.url = WebPushProperties.getUrl();
        this.port = WebPushProperties.getPort();
        this.webSocketGuard = webSocketGuard;
    }

    public SingleSocketHandler(){
        this.url = WebPushProperties.getUrl();
        this.port = WebPushProperties.getPort();
        this.webSocketGuard = new WebSocketGuard() {
            @Override
            public boolean hasPermission(String userId, String token) {
                return true;
            }
        };
    }

    //信息处理
    public void MessageProcessed(String userId,Object msg) {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest){
            //以http请求形式接入，但是走的是websocket
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }else if (msg instanceof  WebSocketFrame){
            //处理websocket客户端的消息
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //添加连接
        ChannelGlobal.getChannelGroup().add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //断开连接
        String userId = (String) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        ChannelGlobal.getChannelGroup().remove(ctx.channel());
        ChannelGlobal.getUserChannelMap().remove(userId);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 异常处理
        cause.printStackTrace();
        ctx.close();
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handShaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 本例程仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", frame.getClass().getName()));
        }
        // 返回应答消息
        String request = ((TextWebSocketFrame) frame).text();
        if(request.equals("heart")){
            //心跳包
            ctx.channel().writeAndFlush(new TextWebSocketFrame("heart"));
            return;
        }
        String userId = (String) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        MessageProcessed(userId,request);
        // 返回【谁发的发给谁】
        // ctx.channel().writeAndFlush(tws);
    }
    /**
     * 唯一的一次http请求，用于创建websocket
     * */
    private void handleHttpRequest(ChannelHandlerContext ctx,
                                   FullHttpRequest req) {
        //要求Upgrade为websocket，过滤掉get/Post
        if (!req.decoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            //若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:"+port+url, null, false);
        handShaker = wsFactory.newHandshaker(req);
        // 获取请求的uri
        String uri = req.uri();
        // 获取请求参数
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        // 获取连接时的userId
        List<String> userIds = parameters.get("userId");
        List<String> tokens = parameters.get("token");
        if (userIds != null && !userIds.isEmpty() && tokens != null && !tokens.isEmpty()) {
            String userId = userIds.get(0);
            String token = tokens.get(0);
            if (!webSocketGuard.hasPermission(userId, token)) {
                sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED));
                return;
            }
            // 将userId和token放入channel的属性中
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
        }
        String userId = (String) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        ChannelGlobal.getUserChannelMap().put(userId, ctx.channel());
        if (handShaker == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handShaker.handshake(ctx.channel(), req);
        }
    }
    /**
     * 拒绝不合法的请求，并返回错误信息
     * */
    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        // 如果是非Keep-Alive，关闭连接
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            /*
            IdleStateEvent event = (IdleStateEvent) evt;
            String evenType = null;
            switch (event.state()) {
                case READER_IDLE:
                    evenType = "读空闲";
                    break;
                case WRITER_IDLE:
                    evenType = "写空闲";
                case ALL_IDLE:
                    evenType = "读写空闲";
                    break;
            }*/
            ctx.channel().close();
        }
    }
}
