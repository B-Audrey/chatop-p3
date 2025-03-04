package oc.chatopbackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


@SpringBootApplication
public class ChatopApplication {

    public static final Logger logger = LoggerFactory.getLogger(ChatopApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ChatopApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logOnStartup() {
        logger.info("Let's go 🚀 !");
    }


}
