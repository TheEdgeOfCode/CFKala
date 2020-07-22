package com.codefathers.cfkclient;

import com.codefathers.cfkclient.utils.Connector;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class CFKClientApplication {
    public static void main(String[] args) {
        Application.launch(CFK.class,args);
    }
}
