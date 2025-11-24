package com.example.demo.logging;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ServerStartupLogger implements ApplicationListener<WebServerInitializedEvent> {

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        String contextPath = event.getApplicationContext().getServerNamespace();
        if (contextPath == null || contextPath.isEmpty()) {
            contextPath = "/";
        }

        System.out.println("\n🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢");
        System.out.println("✅ Tomcat started on port " + port + " (http) with context path '" + contextPath + "'");
        System.out.println("🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢🟢\n");
    }
}