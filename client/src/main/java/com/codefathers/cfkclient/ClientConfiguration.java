package com.codefathers.cfkclient;

import com.codefathers.cfkclient.utils.Connector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Bean
    public Connector connector(){
        return new Connector();
    }
}
