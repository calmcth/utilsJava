package com.wlwx.wlyun.gateway.config;

import com.wlwx.wlyun.gateway.auth.HttpSessionIdHandshakeInterceptor;
import com.wlwx.wlyun.gateway.constant.Constant;
import com.wlwx.wlyun.gateway.constant.MyPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@ConditionalOnWebApplication
//通过EnableWebSocketMessageBroker 开启使用STOMP协议来传输基于代理(message broker)的消息,此时浏览器支持使用@MessageMapping 就像支持@RequestMapping一样。
public class SocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     *  服务器要监听的端口，message会从这里进来，要对这里加一个Handler
     *  这样在网页中就可以通过websocket连接上服务了
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        //注册stomp的节点，映射到指定的url,并指定使用sockjs协议
        stompEndpointRegistry.addEndpoint("/contactChatSocket")
                .setAllowedOrigins("*").addInterceptors(new HttpSessionIdHandshakeInterceptor())
                .setHandshakeHandler(new DefaultHandshakeHandler(){
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        String userId = (String) attributes.get(Constant.USERID);
                        String sessionId = (String) attributes.get(Constant.SESSIONID);
                        return new MyPrincipal(sessionId,userId);
                    }
                })
                .withSockJS();
    }

    //配置消息代理
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // queue、topic、user代理
        // 这是配置到 @MessageMapping Controller
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setUserDestinationPrefix("/user/");
    }


    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(new ChannelInterceptorAdapter() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                System.out.println("send: "+message);
                logger.info("send: "+message);
                return super.preSend(message, channel);
            }
        });
    }




    /**
     * 消息传输参数配置
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(8192) //设置消息字节数大小
                .setSendBufferSizeLimit(8192); //设置消息缓存大小
    }


    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.anyMessage().permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }


}
