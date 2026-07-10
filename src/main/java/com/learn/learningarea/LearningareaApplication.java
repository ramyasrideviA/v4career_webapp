package com.learn.learningarea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.awt.Desktop;
import java.net.ServerSocket;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@EnableScheduling
@SpringBootApplication
public class LearningareaApplication {

    public static void main(String[] args) {

        int port = findFreePort(8080);

        SpringApplication app = new SpringApplication(LearningareaApplication.class);

        Map<String, Object> props = new HashMap<>();
        props.put("server.port", port);

        app.setDefaultProperties(props);

        ConfigurableApplicationContext context = app.run(args);

        System.out.println("=========================================");
        System.out.println("Application Started Successfully");
        System.out.println("Running at: http://localhost:" + port);
        System.out.println("=========================================");
    }

    private static int findFreePort(int startPort) {

        int port = startPort;

        while (true) {
            try (ServerSocket socket = new ServerSocket(port)) {
                return port;
            } catch (Exception e) {
                port++;
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser(ApplicationReadyEvent event) {

        try {

            int port = Integer.parseInt(
                    event.getApplicationContext()
                            .getEnvironment()
                            .getProperty("local.server.port")
            );

            String url = "http://localhost:" + port;

            System.out.println("Opening Browser: " + url);

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Runtime.getRuntime().exec(new String[]{
                        "cmd", "/c", "start", url
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}