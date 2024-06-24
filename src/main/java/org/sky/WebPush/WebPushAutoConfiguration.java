package org.sky.WebPush;

import org.sky.WebPush.Config.WebPushProperties;
import org.sky.WebPush.Service.Impl.PushServiceImpl;
import org.sky.WebPush.Service.PushService;
import org.sky.WebPush.Single.SingleSocketHandler;
import org.sky.WebPush.Single.SingleWebPushClient;
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
            new SingleWebPushClient(WebPushProperties.getPort(),
                    WebPushProperties.getUrl(), singeSocketHandler).run();
        }
        return new PushServiceImpl();
    }
}
