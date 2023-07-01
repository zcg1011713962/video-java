package org.video.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.video.eum.Protocol;
import org.video.manager.CacheManager;
import org.video.properties.WebSocketProperties;
import org.video.websocket.init.WebSocketInitializer;

@Configuration
@ConditionalOnProperty(prefix = "websocket", name = "enable", havingValue = "true")
public class WebSocketConfiguration {

    @Bean
    public WebSocketInitializer websocketInitializer(WebSocketProperties websocketProperties){
        WebSocketInitializer handler = new WebSocketInitializer(websocketProperties.isProxy());
        CacheManager.protocolTable().put(Protocol.WEBSOCKET, handler, false);
        return handler;
    }

}
