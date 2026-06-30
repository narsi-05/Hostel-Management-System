package com.hostel.management.controller;

import com.hostel.management.model.User;
import com.hostel.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Show registration page
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("userTypes", User.UserType.values());
        return "register";
    }

    // Handle registration form submission
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult result,
                           @RequestParam("confirmPassword") String confirmPassword,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        model.addAttribute("userTypes", User.UserType.values());

        if (result.hasErrors()) {
            return "register";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("passwordError", "Passwords do not match!");
            return "register";
        }

        try {
            userService.register(user);
            redirectAttributes.addFlashAttribute("successMessage",
                "Registration successful! Please login to continue.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    // Show login page
    @GetMapping("/login")
    public String showLogin(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid credentials. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        return "login";
    }
}
