package org.sky.WebPush.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExampleConfig {
    @Bean
    public ExampleHandler exampleHandler() {
        return new ExampleHandler();
    }
}
