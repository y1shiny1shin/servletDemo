package com;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;

@WebServlet("/injectWS")
public class InjectWebsocket extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext sc = req.getServletContext();
        ServerContainer serverContainer = (ServerContainer) sc.getAttribute("javax.websocket.server.ServerContainer");
        ServerEndpointConfig config = ServerEndpointConfig.Builder
                .create(MyWebSocketEndpoint.class ,"/ws").build();

        try {
            serverContainer.addEndpoint(config);
            System.out.println("注入成功");
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        }


    }
}
