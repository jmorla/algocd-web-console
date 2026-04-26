package com.algocd.webportal.controllers;

import com.algocd.webportal.config.AuthenticatedUser;
import com.algocd.webportal.entities.User;
import com.algocd.webportal.services.UserService;
import com.algocd.webportal.services.models.CreateUserRecord;
import com.algocd.webportal.util.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private SecurityContextRepository securityContextRepository;

    @InjectMocks
    private RegistrationController registrationController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController).build();
    }

    @Test
    void shouldReturnRegisterViewWithModel() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("createUserRecord"));
    }

    @Test
    void shouldRegisterAndRedirectToDashboard() throws Exception {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@example.com");
        when(userService.createUser(any(CreateUserRecord.class))).thenReturn(Result.success(user));

        UserDetails userDetails = new AuthenticatedUser(
                user.getUserId(), "test@example.com", "password", true, true, true, true, new ArrayList<>()
        );
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

        mockMvc.perform(post("/signup")
                        .param("email", "test@example.com")
                        .param("password", "Password123!")
                        .param("confirmPassword", "Password123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        verify(userDetailsService).loadUserByUsername("test@example.com");
        verify(securityContextRepository).saveContext(any(), any(), any());
    }


    @Test
    void shouldFailValidationAndReturnRegisterView() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "invalid")
                        .param("password", "p")
                        .param("confirmPassword", "p2"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }
}
