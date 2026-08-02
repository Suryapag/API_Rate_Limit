package com.ratelimter.tokenbucket.web;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @RequestMapping("/api/test")
    public String test() {
        System.out.println("http://localhost:8080/api/test called" + System.currentTimeMillis());
        return "http://localhost:8080/api/test called";
    }
}