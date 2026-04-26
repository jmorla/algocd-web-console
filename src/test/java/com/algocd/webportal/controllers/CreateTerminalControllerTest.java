package com.algocd.webportal.controllers;

import com.algocd.webportal.config.AuthenticatedUser;
import com.algocd.webportal.entities.Location;
import com.algocd.webportal.entities.MetaTraderVersion;
import com.algocd.webportal.entities.Plan;
import com.algocd.webportal.mappers.LocationMapper;
import com.algocd.webportal.mappers.PlanMapper;
import com.algocd.webportal.services.TerminalService;
import com.algocd.webportal.services.models.CreateTerminalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CreateTerminalControllerTest {

    private MockMvc createTerminalMockMvc;

    @Mock
    private PlanMapper planMapper;

    @Mock
    private LocationMapper locationMapper;

    @Mock
    private TerminalService terminalService;

    @InjectMocks
    private CreateTerminalController createTerminalController;

    private AuthenticatedUser authenticatedUser;

    @BeforeEach
    void setup() {
        createTerminalMockMvc = MockMvcBuilders.standaloneSetup(createTerminalController).build();
        
        authenticatedUser = new AuthenticatedUser(
            UUID.randomUUID(), "testuser", "password", true, true, true, true, new ArrayList<>()
        );
    }

    @Test
    public void shouldReturnCreateTerminalView() throws Exception {
        Plan plan = new Plan(UUID.randomUUID(), "Standard", 1, 1, new BigDecimal("12.00"), new BigDecimal("0.016"), 1000);
        when(planMapper.findAll()).thenReturn(List.of(plan));

        Location location = new Location(UUID.randomUUID(), "Ashburn", "Northern Virginia", true);
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
        createTerminalMockMvc.perform(post("/terminals")
                        .principal(new UsernamePasswordAuthenticationToken(authenticatedUser, null))
                        .param("version", "METATRADER_5")
                        .param("planId", UUID.randomUUID().toString())
                        .param("locationId", UUID.randomUUID().toString())
                        .param("name", "Test Terminal")
                        .param("accountId", "123456")
                        .param("brokerServer", "TestBroker")
                        .param("password", "secret")
                        .param("tags[0].key", "env")
                        .param("tags[0].value", "prod")
                        .param("tags[1].key", "team")
                        .param("tags[1].value", "alpha"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/terminals"));

        verify(terminalService).createTerminal(any(), any(CreateTerminalRecord.class));
    }

    @Test
    public void shouldReturnToFormOnValidationError() throws Exception {
        createTerminalMockMvc.perform(post("/terminals")
                        .principal(new UsernamePasswordAuthenticationToken(authenticatedUser, null))
                        .param("version", "")
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("create-terminal"));
    }

    @Test
    public void shouldPreserveTagsOnValidationError() throws Exception {
        createTerminalMockMvc.perform(post("/terminals")
                        .principal(new UsernamePasswordAuthenticationToken(authenticatedUser, null))
                        .param("version", "") // Trigger validation error
                        .param("tags[0].key", "env")
                        .param("tags[0].value", "prod"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-terminal"))
                .andExpect(model().attributeExists("terminal"))
                .andExpect(result -> {
                    CreateTerminalRecord terminal = (CreateTerminalRecord) result.getModelAndView().getModel().get("terminal");
                    assertThat(terminal.tags()).hasSize(1);
                    assertThat(terminal.tags().get(0).key()).isEqualTo("env");
                    assertThat(terminal.tags().get(0).value()).isEqualTo("prod");
                });
    }
}
