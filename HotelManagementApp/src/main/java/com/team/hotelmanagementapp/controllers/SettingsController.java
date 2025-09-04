package com.team.hotelmanagementapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@ControllerAdvice
@RequestMapping("/settings")
public class SettingsController {

    @GetMapping
    public String settings() {
        return "settings";
    }
}
