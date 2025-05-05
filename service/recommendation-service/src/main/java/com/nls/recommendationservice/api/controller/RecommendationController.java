package com.nls.recommendationservice.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("recommend")
public class RecommendationController {

    @GetMapping
    public ResponseEntity<String> getUser() {
        return ResponseEntity.ok("hello world");
    }
}
