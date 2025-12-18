package com.example.LabelApp.controller;

import com.example.LabelApp.dto.UserRegistrationDto;
import com.example.LabelApp.models.User;
import com.example.LabelApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new UserRegistrationDto());
        return "registration";
    }

    @PostMapping("/registration")
    public String createUser(@ModelAttribute("userForm") UserRegistrationDto dto, Model model) {
        if (!userService.createUserFromDto(dto)) {
            model.addAttribute("errorMessage", "Пользователь с email " + dto.getEmail() + " уже существует");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/user/{id}")
    public String userInfo(@PathVariable Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        model.addAttribute("tracks", user.getTracks()); // можно заменить на DTO позже
        return "user-info";
    }
}


