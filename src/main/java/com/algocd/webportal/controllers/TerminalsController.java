package com.algocd.webportal.controllers;

import com.algocd.webportal.config.AuthenticatedUser;
import com.algocd.webportal.entities.TerminalSummary;
import com.algocd.webportal.mappers.TerminalMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/terminals")
public class TerminalsController {

    private final TerminalMapper terminalMapper;

    public TerminalsController(TerminalMapper terminalMapper) {
        this.terminalMapper = terminalMapper;
    }

    @GetMapping
    public String terminals(
            @AuthenticationPrincipal AuthenticatedUser user,
            Model model) {
        populateTerminalsModel(user, 1, 10, model);
        return "terminals";
    }

    @GetMapping("/list")
    public String terminalsList(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        populateTerminalsModel(user, page, size, model);
        return "fragments/terminals-list :: terminalsList";
    }

    private void populateTerminalsModel(AuthenticatedUser user, int page, int size, Model model) {
        int offset = (page - 1) * size;
        List<TerminalSummary> terminals = terminalMapper.findTerminalsByUserId(user.getUserId(), size, offset);
        long totalTerminals = terminalMapper.countTerminalsByUserId(user.getUserId());
        int totalPages = (int) Math.ceil((double) totalTerminals / size);

        model.addAttribute("terminals", terminals);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalTerminals", totalTerminals);
        model.addAttribute("pageSize", size);
    }

}
