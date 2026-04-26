package com.algocd.webportal.controllers;

import com.algocd.webportal.entities.User;
import com.algocd.webportal.services.UserService;
import com.algocd.webportal.services.models.CreateUserRecord;
import com.algocd.webportal.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final SecurityContextRepository securityContextRepository;

    public RegistrationController(UserService userService,
                                  UserDetailsService userDetailsService,
                                  SecurityContextRepository securityContextRepository) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.securityContextRepository = securityContextRepository;
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("createUserRecord", new CreateUserRecord("", "", ""));
        return "register";
    }

    @PostMapping("/signup")
    public String register(@Valid @ModelAttribute("createUserRecord") CreateUserRecord createUserRecord,
                           BindingResult bindingResult,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        Result<User> result = userService.createUser(createUserRecord);

        User user = result.getValue();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(auth);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        return "redirect:/dashboard";
    }
}
