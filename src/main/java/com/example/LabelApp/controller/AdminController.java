package com.example.LabelApp.controller;

import com.example.LabelApp.models.User;
import com.example.LabelApp.models.enums.Role;
import com.example.LabelApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("users", userService.list());
        return "admin";
    }

    @PostMapping("/admin/user/ban/{id}")
    public String userBan(@PathVariable("id") Long id) {
        userService.banUser(id);
        return "redirect:/admin";
    }

    // ====== GET: форма редактирования ======
    @GetMapping("/admin/user/edit/{id:\\d+}")
    public String userEdit(@PathVariable Long id, Model model) {
        User user = userService.getById(id);

        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());

        return "user-edit";
    }

    // ====== POST: сохранение ======
    @PostMapping("/admin/user/edit")
    public String userEdit(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "roles", required = false) String[] roles
    ) {
        User user = userService.getById(userId);
        userService.changeUserRoles(user, roles);
        return "redirect:/admin";
    }
}

