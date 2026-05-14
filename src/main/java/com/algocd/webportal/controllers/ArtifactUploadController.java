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

import com.algocd.webportal.entities.ArtifactProcessingQueue;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/repository")
public class ArtifactUploadController {

    private final ArtifactService artifactService;

    public ArtifactUploadController(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    @GetMapping("/upload")
    public String upload(HttpSession session, Model model) {
        session.removeAttribute("currentUploadBatchId");
        model.addAttribute("isProcessing", false);
        return "upload-artifacts";
    }

    @PostMapping("/upload")
    public String handleUpload(
            @RequestParam("files") MultipartFile[] files,
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            HttpSession session,
            Model model,
            HttpServletResponse response) {

        Result<UUID> result = artifactService.processAndQueueArtifacts(files, currentUser.getUserId());

        if (result.isFailure()) {
            response.setHeader("HX-Redirect", "/repository/upload?error=true");
            return null;
        }

        UUID batchId = result.getValue();
        session.setAttribute("currentUploadBatchId", batchId);
        
        List<ArtifactProcessingQueue> queueFiles = artifactService.getQueueStatus(batchId);
        model.addAttribute("queueFiles", queueFiles);
        model.addAttribute("isProcessing", true);

        return "fragments/upload-file-list :: uploadFileList";
    }

    @GetMapping("/upload/status")
    public String uploadStatus(HttpSession session, Model model) {
        UUID batchId = (UUID) session.getAttribute("currentUploadBatchId");
        if (batchId != null) {
            List<ArtifactProcessingQueue> queueFiles = artifactService.getQueueStatus(batchId);
            model.addAttribute("queueFiles", queueFiles);
            
            boolean isProcessing = queueFiles.stream().anyMatch(file -> 
                file.getStatus() != com.algocd.webportal.entities.ProcessingStatus.COMPLETED &&
                file.getStatus() != com.algocd.webportal.entities.ProcessingStatus.FAILED
            );
            model.addAttribute("isProcessing", isProcessing);
        }
        return "fragments/upload-file-list :: uploadFileList";
    }
}
