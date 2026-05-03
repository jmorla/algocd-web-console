package com.algocd.webportal.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/repository")
public class ArtifactUploadController {

    @GetMapping("/upload")
    public String upload() {
        return "upload-artifacts";
    }
}
