package com.juan.springboot.angeluz.services.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServiceController {

    @GetMapping("index")
    public String index() {
        return "index";
    }
}
