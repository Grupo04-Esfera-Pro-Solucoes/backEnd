package com.esfera.g2.esferag2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MappingController {
    @GetMapping("/")
    public String start() {
        return "login";
    }

    @GetMapping("/nav/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("selectedScreen", "dashboard");
        return "home";
    }

    @GetMapping("/nav/configuration")
    public String configuration(Model model) {
        model.addAttribute("selectedScreen", "configuration");
        return "configuration";
    }

    @GetMapping("/nav/lead")
    public String lead(Model model) {
        model.addAttribute("selectedScreen", "lead");
        return "lead";
    }

    @GetMapping("/nav/client")
    public String client(Model model) {
        model.addAttribute("selectedScreen", "client");
        return "client";
    }

    @GetMapping("/nav/proposal")
    public String proposal(Model model) {
        model.addAttribute("selectedScreen", "proposal");
        return "proposal";
    }
}
