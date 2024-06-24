package org.sky.WebPush.Global;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelGlobal {
    /**
     * 定义一个channel组，管理所有的channel * GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
     * -- GETTER --
     *  获取channel组

     */
    @Getter
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 存放用户与Chanel的对应信息，用于给指定用户发送消息
     * -- GETTER --
     *  获取用户channel map

     */
    @Getter
    private static ConcurrentHashMap<String, Channel> userChannelMap = new ConcurrentHashMap<>();

}
