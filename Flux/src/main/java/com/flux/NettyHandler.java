package com.flux;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NettyHandler {

    @RequestMapping("/nettyHandler")
    public String nettyHandler() {
        Thread thread =null;
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getClass().getName().contains("NettyWebServer")) {
                thread = t;
            }
        }
        // TODO
        return "";

    }
}
