package org.sky.WebPush.Config;

import org.sky.WebPush.Service.Impl.WebSocketPushServiceImpl;
import org.sky.WebPush.Service.PushService;
import org.sky.WebPush.Single.SingleSocketHandler;
import org.sky.WebPush.Single.SingleWebPushServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties(WebPushProperties.class)
public class WebPushAutoConfiguration {

    @Resource
    private SingleSocketHandler singeSocketHandler;

    @Bean
    @ConditionalOnMissingBean
    public PushService init() throws Exception {
        if (singeSocketHandler == null) {
            throw new RuntimeException("singeSocketHandler is null");
        }
        if ("single".equals(WebPushProperties.getMode())) {
            //多线程启动
            new Thread(() -> {
                try {
                    new SingleWebPushServer(WebPushProperties.getPort(),
                            WebPushProperties.getUrl(), singeSocketHandler).run();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        return new WebSocketPushServiceImpl();
    }
}
