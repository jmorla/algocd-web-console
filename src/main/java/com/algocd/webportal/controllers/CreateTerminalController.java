package com.algocd.webportal.controllers;

import com.algocd.webportal.config.AuthenticatedUser;
import com.algocd.webportal.entities.Location;
import com.algocd.webportal.entities.Plan;
import com.algocd.webportal.mappers.LocationMapper;
import com.algocd.webportal.mappers.PlanMapper;
import com.algocd.webportal.services.TerminalService;
import com.algocd.webportal.services.models.CreateTerminalRecord;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class CreateTerminalController {

    private final PlanMapper planMapper;
    private final LocationMapper locationMapper;
    private final TerminalService terminalService;

    public CreateTerminalController(PlanMapper planMapper, LocationMapper locationMapper, TerminalService terminalService) {
        this.planMapper = planMapper;
        this.locationMapper = locationMapper;
        this.terminalService = terminalService;
    }

    @ModelAttribute("plans")
    public List<Plan> plans() {
        return planMapper.findAll();
    }

    @ModelAttribute("locations")
    public List<Location> locations() {
        return locationMapper.findAll();
    }

    @GetMapping("/terminals/new")
    public String createTerminal(@ModelAttribute("plans") List<Plan> plans,
                                 @ModelAttribute("locations") List<Location> locations,
                                 Model model) {
        model.addAttribute("terminal", new CreateTerminalRecord(
            null, null, null, new ArrayList<>(), "", "", "", ""
        ));
        return "create-terminal";
    }

    @PostMapping("/terminals")
    public String processCreateTerminal(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                        @Valid @ModelAttribute("terminal") CreateTerminalRecord terminal,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "create-terminal";
        }

        if (authenticatedUser == null) {
            throw new IllegalStateException("User not authenticated");
        }

        terminalService.createTerminal(authenticatedUser.getUserId(), terminal);

        return "redirect:/terminals";
    }
}
