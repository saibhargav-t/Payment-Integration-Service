package com.hulkhiretech.payments.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
@RequestMapping("/payments")
public class AdditionController {

    @PostMapping("/add")
    public int addNumbers(@RequestParam int val1, @RequestParam int val2) {
        log.info("Value 1 is {}", val1);
        log.info("Value 2 is {}", val2);
        log.info("Addition of {} and {} is {}", val1, val2, (val1+val2));
        return val1+val2;
    }
    
}
