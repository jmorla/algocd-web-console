package com.algocd.webportal.controllers;

import com.algocd.webportal.services.TerminalService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/terminals")
public class TerminalsController {

    private final TerminalService terminalService;

    public TerminalsController(TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    @GetMapping
    public String terminals() {
        return "terminals";
    }

}
