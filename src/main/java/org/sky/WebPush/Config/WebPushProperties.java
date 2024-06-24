package org.sky.WebPush.Config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "webpush") // 自动获取配置文件中前缀为http的属性，把值传入对象参数
public class WebPushProperties {
    @Getter
    private static int port = 233;
    @Getter
    private static String url = "/ws";
    @Getter
    private static String mode = "single";
}
