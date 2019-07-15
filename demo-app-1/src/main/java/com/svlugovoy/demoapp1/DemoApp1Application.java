package com.svlugovoy.demoapp1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApp1Application {

    public static void main(String[] args) {
        SpringApplication.run(DemoApp1Application.class, args);
    }

    @GetMapping("/world")
    public String print() throws InterruptedException {
        Thread.sleep(5000);
        return "World";
    }

}
