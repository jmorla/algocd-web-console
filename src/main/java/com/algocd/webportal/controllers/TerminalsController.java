package com.algocd.webportal.controllers;

import com.algocd.webportal.services.models.CreateTerminalRecord;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/terminals")
public class TerminalsController {


    @GetMapping
    public String terminals() {
        return "terminals";
    }

}
