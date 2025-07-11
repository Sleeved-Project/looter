package com.sleeved.looter.infra.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class ApplicationLifecycleManager implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Looter Application launched");
        log.info("📡 Application ready to receive jobs commands");
        
        new CountDownLatch(1).await();
    }
}