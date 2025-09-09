package com.team.hotelmanagementapp.controllers;

import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.services.UserService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@ControllerAdvice
@RequestMapping("/settings")
public class SettingsController {
    
    @Autowired 
    private UserService userService;
    
    @GetMapping
    public String settings(Model model, Principal principal) {
        User user = userService.getByUsername(principal.getName());
        model.addAttribute("user", user);
        return "settings";
    }
}
