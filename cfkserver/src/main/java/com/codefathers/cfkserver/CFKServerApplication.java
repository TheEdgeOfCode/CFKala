package com.codefathers.cfkserver;

import com.codefathers.cfkserver.service.file.StorageService;
import com.codefathers.cfkserver.utils.JwtUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({StorageService.class})
public class CFKServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CFKServerApplication.class, args);
    }
}
