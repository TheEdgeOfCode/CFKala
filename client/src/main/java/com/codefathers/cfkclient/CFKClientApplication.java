package com.codefathers.cfkclient;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CFKClientApplication {
    public static void main(String[] args) {
        Application.launch(CFK.class,args);
    }
}
