package com.algocd.webportal.controllers;

import com.algocd.webportal.config.AuthenticatedUser;
import com.algocd.webportal.entities.Artifact;
import com.algocd.webportal.entities.ArtifactType;
import com.algocd.webportal.mappers.ArtifactMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RepositoryController {

    private final ArtifactMapper artifactMapper;

    public RepositoryController(ArtifactMapper artifactMapper) {
        this.artifactMapper = artifactMapper;
    }

    @GetMapping("/repository")
    public String repository(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "1") int expertPage,
            @RequestParam(defaultValue = "1") int indicatorPage,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        populateArtifacts(user, ArtifactType.EXPERT, expertPage, size, model, "experts", "expertPage", "totalExpertPages", "totalExperts");
        populateArtifacts(user, ArtifactType.INDICATOR, indicatorPage, size, model, "indicators", "indicatorPage", "totalIndicatorPages", "totalIndicators");
        
        model.addAttribute("pageSize", size);

        return "repository";
    }

    @GetMapping("/repository/list")
    public String repositoryList(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam ArtifactType type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        int offset = (page - 1) * size;
        List<Artifact> artifacts = artifactMapper.findArtifactsByUserIdAndType(user.getUserId(), type, size, offset);
        long totalCount = artifactMapper.countArtifactsByUserIdAndType(user.getUserId(), type);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("artifacts", artifacts);
        model.addAttribute("type", type.name());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("size", size);

        return "fragments/artifacts-list :: artifactsList";
    }

    private void populateArtifacts(AuthenticatedUser user, ArtifactType type, int page, int size, Model model, 
                                   String listAttr, String pageAttr, String totalPagesAttr, String totalCountAttr) {
        int offset = (page - 1) * size;
        List<Artifact> artifacts = artifactMapper.findArtifactsByUserIdAndType(user.getUserId(), type, size, offset);
        long totalCount = artifactMapper.countArtifactsByUserIdAndType(user.getUserId(), type);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute(listAttr, artifacts);
        model.addAttribute(pageAttr, page);
        model.addAttribute(totalPagesAttr, totalPages);
        model.addAttribute(totalCountAttr, totalCount);
        model.addAttribute("pageSize", size);
    }
}
