package com.flux;

import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.handler.FilteringWebHandler;

@RestController
public class WebFilter {
    @RequestMapping("/webFilter")
    public String webFilter() throws Exception{
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getClass().getName().contains("NettyWebServer")) {
                /**
                 * org.springframework.boot.web.embedded.netty.NettyWebServer@4a9e5ba8
                 * org.springframework.http.server.reactive.ReactorHttpHandlerAdapter@364c0898
                 * org.springframework.boot.web.reactive.context.WebServerManager$DelayedInitializationHttpHandler@e3746e2
                 * HttpWebHandlerAdapter [delegate=ExceptionHandlingWebHandler [delegate=FilteringWebHandler [delegate=org.springframework.web.reactive.DispatcherHandler@55f4d1bf]]]
                 * ExceptionHandlingWebHandler [delegate=FilteringWebHandler [delegate=org.springframework.web.reactive.DispatcherHandler@55f4d1bf]]
                 * FilteringWebHandler [delegate=org.springframework.web.reactive.DispatcherHandler@55f4d1bf]
                 */
                NettyWebServer o1 = (NettyWebServer) Utils.getField(thread ,"this$0");
                System.out.println(o1);
                ReactorHttpHandlerAdapter o2 = (ReactorHttpHandlerAdapter) Utils.getField(o1 ,"handler");
                System.out.println(o2);
                Object o3 = Utils.getField(o2 ,"httpHandler");
                System.out.println(o3);
                Object o4 = Utils.getField(o3 ,"delegate");
                System.out.println(o4);
                Object o5 = Utils.getField(o4 ,"delegate");
                System.out.println(o5);
                FilteringWebHandler o6 = (FilteringWebHandler) Utils.getField(o5 ,"delegate");
                System.out.println(o6);
                // TODO

            }
        }

        return "success";
    }
}
