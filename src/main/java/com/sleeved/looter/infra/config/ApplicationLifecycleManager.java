package com.sleeved.looter.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class ApplicationLifecycleManager implements CommandLineRunner {

    @Value("${spring.main.web-application-type:servlet}")
    private String webApplicationType;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Looter Application launched");
        
        // Mode job : application éphémère
        if ("none".equals(webApplicationType)) {
            log.info("Running in job mode - application will terminate after job completion");
            return;
        }
        
        // Mode service : application persistante
        log.info("📡 Running in service mode - application ready to receive requests");
        new CountDownLatch(1).await();
    }
}