package com.algocd.webportal.services;

import com.algocd.webportal.mappers.TagMapper;
import com.algocd.webportal.mappers.TerminalMapper;
import com.algocd.webportal.util.Result;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@Validated
public class TerminalServiceImpl implements TerminalService {

    private final TerminalMapper terminalMapper;
    private final TagMapper tagMapper;
    private final Validator validator;

    public TerminalServiceImpl(TerminalMapper terminalMapper, TagMapper tagMapper, Validator validator) {
        this.terminalMapper = terminalMapper;
        this.tagMapper = tagMapper;
        this.validator = validator;
    }
}
