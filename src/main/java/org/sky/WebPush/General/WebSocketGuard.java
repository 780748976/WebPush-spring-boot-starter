package org.sky.WebPush.General;

public interface WebSocketGuard {
    //判断是否有权限
    public boolean hasPermission(String userId, String token);
}
