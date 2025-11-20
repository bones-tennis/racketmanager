package com.example.racketmanager.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }

        for (GrantedAuthority g : auth.getAuthorities()) {
            String authority = g.getAuthority();
            if (authority.equals("ROLE_STAFF")) {
                return "redirect:/staff/orders";
            }
            if (authority.equals("ROLE_CUSTOMER")) {
                return "redirect:/customer/orders";
            }
        }

        return "redirect:/login";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
