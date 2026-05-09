package com.algocd.webportal.controllers;

import com.algocd.webportal.entities.Platform;
import com.algocd.webportal.entities.Terminal;
import com.algocd.webportal.entities.TerminalSummary;
import com.algocd.webportal.mappers.TerminalMapper;
import com.algocd.webportal.services.TerminalService;
import com.algocd.webportal.services.models.CreateTerminalRequest;
import com.algocd.webportal.util.Result;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/terminals")
public class TerminalsController {

    private final TerminalMapper terminalMapper;
    private final TerminalService terminalService;

    public TerminalsController(TerminalMapper terminalMapper, TerminalService terminalService) {
        this.terminalMapper = terminalMapper;
        this.terminalService = terminalService;
    }

    @GetMapping
    public String terminals(Model model) {
        populateTerminalsModel(1, 10, model);
        return "terminals";
    }

    @GetMapping("/list")
    public String terminalsList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        populateTerminalsModel(page, size, model);
        return "fragments/terminals-list :: terminalsList";
    }

    @GetMapping("/statuses")
    public String terminalStatuses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        int offset = (page - 1) * size;
        List<TerminalSummary> terminals = terminalMapper.findAllStatuses(size, offset);
        model.addAttribute("terminals", terminals);
        return "fragments/terminal-status :: oob-updates";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("platforms", Platform.values());
        model.addAttribute("createTerminalRequest", new CreateTerminalRequest("", null, new HashMap<>()));
        return "fragments/terminal-create-modal :: terminalCreateForm";
    }

    @PostMapping
    public String createTerminal(
            @Valid @ModelAttribute("createTerminalRequest") CreateTerminalRequest request,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("platforms", Platform.values());
            return "fragments/terminal-create-modal :: terminalCreateForm";
        }

        Result<Terminal> result = terminalService.createTerminal(request);
        if (result.isSuccess()) {
            model.addAttribute("terminal", result.getValue());
            return "fragments/terminal-create-modal :: terminalTokenView";
        } else {
            model.addAttribute("error", result.getError().getMessage());
            model.addAttribute("platforms", Platform.values());
            return "fragments/terminal-create-modal :: terminalCreateForm";
        }
    }

    private void populateTerminalsModel(int page, int size, Model model) {
        int offset = (page - 1) * size;
        List<TerminalSummary> terminals = terminalMapper.findAllTerminals(size, offset);
        long totalTerminals = terminalMapper.countAllTerminals();
        int totalPages = (int) Math.ceil((double) totalTerminals / size);

        model.addAttribute("terminals", terminals);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalTerminals", totalTerminals);
        model.addAttribute("pageSize", size);
    }

}
