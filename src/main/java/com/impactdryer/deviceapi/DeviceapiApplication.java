package com.impactdryer.deviceapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class DeviceapiApplication {

    static void main(String[] args) {
        SpringApplication.run(DeviceapiApplication.class, args);
    }
}
