package com.eventaro.Eventaro.presentation.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EasterEggController {

    @GetMapping("/secret/doom")
    public String playDoom() {
        return "secret/doom";
    }
}