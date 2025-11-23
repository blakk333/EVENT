package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.datatransfer.UserRegistrationDto;
import com.eventaro.Eventaro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userDto") @Valid UserRegistrationDto userDto,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.registerCustomer(userDto);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
}