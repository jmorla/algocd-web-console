package com.algocd.webportal.services;

import com.algocd.webportal.mappers.TagMapper;
import com.algocd.webportal.mappers.TerminalMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TerminalServiceImplTest {

    @Mock
    private TerminalMapper terminalMapper;

    @Mock
    private TagMapper tagMapper;

    private TerminalServiceImpl terminalService;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        terminalService = new TerminalServiceImpl(terminalMapper, tagMapper, validator);
    }
}
