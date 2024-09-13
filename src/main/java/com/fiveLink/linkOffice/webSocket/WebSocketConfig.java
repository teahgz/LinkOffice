package com.fiveLink.linkOffice.webSocket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
//@EnableWebSocketMessageBroker //STOMP 사용
public class WebSocketConfig implements WebSocketConfigurer {
//      기존 사용한 config
    private final ChatWebSocketHandler chatWebSocketHandler;
    @Autowired
    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/websocket/chat").setAllowedOrigins("*");
    }


}
