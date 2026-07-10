package com.ratelimiter.fixedwindow.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class statuscheck {
    @RequestMapping("/status")
    public ResponseEntity<?> status() {
        StatusResponse response = new StatusResponse();
        response.setCode("200");
        response.setMessage("Service is running");
        return ResponseEntity.ok(response);
    }
}

@lombok.Data
class StatusResponse {
    String code;
    String message;
}