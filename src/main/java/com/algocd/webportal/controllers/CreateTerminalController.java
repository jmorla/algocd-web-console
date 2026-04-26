package com.algocd.webportal.controllers;

import com.algocd.webportal.entities.Location;
import com.algocd.webportal.entities.Plan;
import com.algocd.webportal.mappers.LocationMapper;
import com.algocd.webportal.mappers.PlanMapper;
import com.algocd.webportal.services.models.CreateTerminalRecord;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CreateTerminalController {

    private final PlanMapper planMapper;
    private final LocationMapper locationMapper;

    public CreateTerminalController(PlanMapper planMapper, LocationMapper locationMapper) {
        this.planMapper = planMapper;
        this.locationMapper = locationMapper;
    }

    @GetMapping("/terminals/new")
    public String createTerminal(Model model) {
        List<Plan> plans = planMapper.findAll();
        List<Location> locations = locationMapper.findAll();
        
        model.addAttribute("plans", plans);
        model.addAttribute("locations", locations);
        
        String defaultPlanId = plans.isEmpty() ? "" : plans.getFirst().getPlanId();
        String defaultLocationId = locations.isEmpty() ? "" : locations.getFirst().getLocationId();
        
        model.addAttribute("terminal", new CreateTerminalRecord(
            "mt4", defaultPlanId, defaultLocationId, new ArrayList<>(), "", "", "", ""
        ));
        return "create-terminal";
    }
}
