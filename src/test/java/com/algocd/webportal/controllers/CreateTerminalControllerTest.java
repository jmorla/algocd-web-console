package com.algocd.webportal.controllers;

import com.algocd.webportal.entities.Location;
import com.algocd.webportal.entities.Plan;
import com.algocd.webportal.mappers.LocationMapper;
import com.algocd.webportal.mappers.PlanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CreateTerminalControllerTest {

    private MockMvc createTerminalMockMvc;
    private MockMvc terminalsMockMvc;

    @Mock
    private PlanMapper planMapper;

    @Mock
    private LocationMapper locationMapper;

    @InjectMocks
    private CreateTerminalController createTerminalController;

    @InjectMocks
    private TerminalsController terminalsController;

    @BeforeEach
    void setup() {
        createTerminalMockMvc = MockMvcBuilders.standaloneSetup(createTerminalController).build();
        terminalsMockMvc = MockMvcBuilders.standaloneSetup(terminalsController).build();
    }

    @Test
    public void shouldReturnCreateTerminalView() throws Exception {
        Plan plan = new Plan("1gb", "Standard", 1, 1, new BigDecimal("12.00"), new BigDecimal("0.016"), 1000);
        when(planMapper.findAll()).thenReturn(List.of(plan));

        Location location = new Location("virginia", "Ashburn", "Northern Virginia", true);
        when(locationMapper.findAll()).thenReturn(List.of(location));

        createTerminalMockMvc.perform(get("/terminals/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-terminal"))
                .andExpect(model().attributeExists("terminal"))
                .andExpect(model().attributeExists("plans"))
                .andExpect(model().attributeExists("locations"))
                .andExpect(model().attribute("plans", List.of(plan)))
                .andExpect(model().attribute("locations", List.of(location)));
    }

    @Test
    public void shouldHandleTerminalCreation() throws Exception {
        terminalsMockMvc.perform(post("/terminals")
                        .param("version", "mt5")
                        .param("plan", "4gb")
                        .param("location", "newyork")
                        .param("name", "Test Terminal")
                        .param("accountId", "123456")
                        .param("brokerServer", "TestBroker")
                        .param("password", "secret")
                        .param("tags", "tag1", "tag2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/terminals"));
    }

    @Test
    public void shouldReturnToFormOnValidationError() throws Exception {
        // standaloneSetup needs a validator to actually perform validation
        // but we can at least check if the mapping is there.
        // For a more complete test, we'd need to configure the validator.
        terminalsMockMvc.perform(post("/terminals")
                        .param("version", "")
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("create-terminal"));
    }
}
