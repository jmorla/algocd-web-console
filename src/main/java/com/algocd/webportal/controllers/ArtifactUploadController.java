package com.algocd.webportal.controllers;

import com.algocd.webportal.config.AuthenticatedUser;
import com.algocd.webportal.services.ArtifactService;
import com.algocd.webportal.util.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/repository")
public class ArtifactUploadController {

    private final ArtifactService artifactService;

    public ArtifactUploadController(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload-artifacts";
    }

    @PostMapping("/upload")
    public void handleUpload(
            @RequestParam("files") MultipartFile[] files,
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            HttpServletResponse response) {

        Result<Void> result = artifactService.processAndQueueArtifacts(files, currentUser.getUserId());

        if (result.isFailure()) {
            response.setHeader("HX-Redirect", "/repository/upload?error=true");
            return;
        }

        response.setHeader("HX-Redirect", "/repository");
    }
}
