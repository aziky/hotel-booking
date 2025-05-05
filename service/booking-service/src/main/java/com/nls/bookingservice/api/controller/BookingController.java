package com.nls.bookingservice.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("booking")
public class BookingController {

    @GetMapping
    public ResponseEntity<String> getUser() {
        return ResponseEntity.ok("hello world");
    }

}
