package org.sky.WebPush.test;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.sky.WebPush.Utils.SseEmitterUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/sse")
public class testController {

    /**
     * 用于创建连接
     */
    @GetMapping("/connect/{userId}")
    @CrossOrigin
    public SseEmitter connect(@PathVariable String userId) {
        return SseEmitterUtil.connect(userId);
    }

    /**
     * 关闭连接
     */
    @GetMapping("/close/{userid}")
    @CrossOrigin
    public void close(@PathVariable("userid") String userid) {
        SseEmitterUtil.removeUser(userid);
    }

    @GetMapping("/sse")
    @CrossOrigin
    public void sse(){
        // 构建推送消息体
        SseEmitterUtil.sendMessage("2", "6");
    }
}