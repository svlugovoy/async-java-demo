package com.svlugovoy.demoapp2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApp2Application {

    public static void main(String[] args) {
        SpringApplication.run(DemoApp2Application.class, args);
    }


    @GetMapping("/hello")
    public String print() throws InterruptedException {
        Thread.sleep(5000);
        return "Hello";
    }

}
