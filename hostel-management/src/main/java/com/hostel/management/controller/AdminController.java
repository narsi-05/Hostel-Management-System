package com.hostel.management.controller;

import com.hostel.management.model.*;
import com.hostel.management.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Admin Controller - manages users, hostels, and system overview.
 * Admin logs in with: admin@hostel.com / admin123
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private HostelService hostelService;
    @Autowired private MessageService messageService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalOwners",   userService.countOwners());
        model.addAttribute("totalVisitors", userService.countVisitors());
        model.addAttribute("totalHostels",  hostelService.countActiveHostels());
        model.addAttribute("recentHostels", hostelService.getAllHostels().stream().limit(5).toList());
        model.addAttribute("recentUsers",   userService.getAllUsers().stream().limit(5).toList());
        return "admin/dashboard";
    }

    // ===== USER MANAGEMENT =====

    @GetMapping("/users")
    public String listUsers(@RequestParam(required = false) String type, Model model) {
        List<User> users;
        if ("OWNER".equals(type)) {
            users = userService.getUsersByType(User.UserType.OWNER);
        } else if ("VISITOR".equals(type)) {
            users = userService.getUsersByType(User.UserType.VISITOR);
        } else {
            users = userService.getAllUsers();
        }
        model.addAttribute("users", users);
        model.addAttribute("filterType", type);
        return "admin/users";
    }

    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        return userService.findById(id).map(user -> {
            model.addAttribute("user", user);
            return "admin/user-detail";
        }).orElse("redirect:/admin/users");
    }

    @PostMapping("/users/{id}/block")
    public String blockUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.blockUser(id);
        ra.addFlashAttribute("successMessage", "User has been blocked.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/unblock")
    public String unblockUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.unblockUser(id);
        ra.addFlashAttribute("successMessage", "User has been unblocked.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("successMessage", "User deleted successfully.");
        return "redirect:/admin/users";
    }

    // ===== HOSTEL MANAGEMENT =====

    @GetMapping("/hostels")
    public String listHostels(Model model) {
        model.addAttribute("hostels", hostelService.getAllHostels());
        return "admin/hostels";
    }

    @GetMapping("/hostels/{id}")
    public String viewHostel(@PathVariable Long id, Model model) {
        return hostelService.findById(id).map(hostel -> {
            model.addAttribute("hostel", hostel);
            model.addAttribute("reviews", hostelService.getHostelReviews(hostel));
            return "admin/hostel-detail";
        }).orElse("redirect:/admin/hostels");
    }

    @PostMapping("/hostels/{id}/delete")
    public String deleteHostel(@PathVariable Long id, RedirectAttributes ra) {
        hostelService.deleteHostel(id);
        ra.addFlashAttribute("successMessage", "Hostel deleted successfully.");
        return "redirect:/admin/hostels";
    }
}
