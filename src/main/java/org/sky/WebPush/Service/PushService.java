package org.sky.WebPush.Service;

public interface PushService {
    // 向单个用户推送消息
    public void pushMessage(String userId, Object message);
    // 向所有推送消息
    public void pushAllMessage(String message);
    // 获取当前连接数
    public int getConnectNum();
}
