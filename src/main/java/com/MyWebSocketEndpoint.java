package com;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.IOException;

public class MyWebSocketEndpoint extends Endpoint {
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("启动 WS");
        MessageHandler handler = new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String s) {
                System.out.println("Received: " + s);

                try {
                    session.getBasicRemote().sendText("hello");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        session.addMessageHandler(handler);
    }
}
