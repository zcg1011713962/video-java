package org.video.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.video.eum.Protocol;
import org.video.manager.CacheManager;
import org.video.properties.RtspProperties;
import org.video.rtsp.init.RtpServerlInitializer;
import org.video.rtsp.init.RtspClientlInitializer;

@Configuration
@ConditionalOnProperty(prefix = "rtsp", name = "enable", havingValue = "true")
public class RtspConfiguration {

    @Bean
    public RtspClientlInitializer rtspClientlInitializer(RtspProperties rtspProperties){
        RtspClientlInitializer handler = new RtspClientlInitializer(rtspProperties.isProxy());
        CacheManager.protocolTable().put(Protocol.RTSP, handler, false);
        return handler;
    }

    @Bean
    public RtpServerlInitializer rtpServerlInitializer(RtspProperties rtspProperties){
        RtpServerlInitializer handler = new RtpServerlInitializer(rtspProperties.isProxy());
        CacheManager.protocolTable().put(Protocol.RTP, handler, false);
        return handler;
    }


}
