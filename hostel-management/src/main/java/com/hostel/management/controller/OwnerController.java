package com.hostel.management.controller;

import com.hostel.management.model.*;
import com.hostel.management.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/owner")
public class OwnerController {

    @Autowired private UserService userService;
    @Autowired private HostelService hostelService;
    @Autowired private MessageService messageService;

    private User getLoggedInUser(Authentication auth) {
        return userService.findByEmailOrPhone(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User owner = getLoggedInUser(auth);
        List<Hostel> hostels = hostelService.getHostelsByOwner(owner);
        long unread = messageService.countUnreadMessages(owner);

        model.addAttribute("owner", owner);
        model.addAttribute("hostels", hostels);
        model.addAttribute("totalHostels", hostels.size());
        model.addAttribute("unreadMessages", unread);
        return "owner/dashboard";
    }

    // ===== HOSTEL CRUD =====

    @GetMapping("/hostel/add")
    public String showAddHostel(Model model) {
        model.addAttribute("hostel", new Hostel());
        model.addAttribute("hostelTypes",    Hostel.HostelType.values());
        model.addAttribute("availabilities", Hostel.Availability.values());
        model.addAttribute("sharingTypes",   Hostel.SharingType.values());
        return "owner/add-hostel";
    }

    @PostMapping("/hostel/add")
    public String addHostel(@Valid @ModelAttribute("hostel") Hostel hostel,
                            BindingResult result,
                            Authentication auth,
                            Model model,
                            RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Please fix the highlighted errors and try again.");
            model.addAttribute("hostelTypes",    Hostel.HostelType.values());
            model.addAttribute("availabilities", Hostel.Availability.values());
            model.addAttribute("sharingTypes",   Hostel.SharingType.values());
            return "owner/add-hostel";
        }
        try {
            User owner = getLoggedInUser(auth);
            hostelService.addHostel(hostel, owner);
            ra.addFlashAttribute("successMessage", "Hostel added successfully!");
            return "redirect:/owner/dashboard";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding hostel: " + e.getMessage());
            model.addAttribute("hostelTypes",    Hostel.HostelType.values());
            model.addAttribute("availabilities", Hostel.Availability.values());
            model.addAttribute("sharingTypes",   Hostel.SharingType.values());
            return "owner/add-hostel";
        }
    }

    @GetMapping("/hostel/edit/{id}")
    public String showEditHostel(@PathVariable Long id, Authentication auth, Model model) {
        User owner = getLoggedInUser(auth);
        return hostelService.findById(id).filter(h -> h.getOwner().getId().equals(owner.getId()))
                .map(hostel -> {
                    model.addAttribute("hostel", hostel);
                    model.addAttribute("hostelTypes",    Hostel.HostelType.values());
                    model.addAttribute("availabilities", Hostel.Availability.values());
                    model.addAttribute("sharingTypes",   Hostel.SharingType.values());
                    return "owner/edit-hostel";
                }).orElse("redirect:/owner/dashboard");
    }

    @PostMapping("/hostel/edit/{id}")
    public String editHostel(@PathVariable Long id,
                             @Valid @ModelAttribute("hostel") Hostel hostel,
                             BindingResult result,
                             Authentication auth,
                             Model model,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Please fix the highlighted errors and try again.");
            model.addAttribute("hostelTypes",    Hostel.HostelType.values());
            model.addAttribute("availabilities", Hostel.Availability.values());
            model.addAttribute("sharingTypes",   Hostel.SharingType.values());
            return "owner/edit-hostel";
        }
        try {
            hostel.setId(id);
            hostelService.updateHostel(hostel);
            ra.addFlashAttribute("successMessage", "Hostel updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error updating hostel.");
        }
        return "redirect:/owner/dashboard";
    }

    @PostMapping("/hostel/delete/{id}")
    public String deleteHostel(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        User owner = getLoggedInUser(auth);
        hostelService.findById(id).filter(h -> h.getOwner().getId().equals(owner.getId()))
                .ifPresent(h -> hostelService.deleteHostel(id));
        ra.addFlashAttribute("successMessage", "Hostel removed.");
        return "redirect:/owner/dashboard";
    }

    // ===== MESSAGES =====

    @GetMapping("/messages")
    public String messages(Authentication auth, Model model) {
        User owner = getLoggedInUser(auth);
        model.addAttribute("messages", messageService.getAllMessagesForUser(owner));
        model.addAttribute("owner", owner);
        messageService.markAllAsReadForUser(owner);
        return "owner/messages";
    }

    @PostMapping("/messages/reply")
    public String reply(@RequestParam Long receiverId,
                        @RequestParam String content,
                        Authentication auth,
                        RedirectAttributes ra) {
        User owner = getLoggedInUser(auth);
        userService.findById(receiverId).ifPresent(receiver ->
                messageService.sendMessage(owner, receiver, content, null));
        ra.addFlashAttribute("successMessage", "Reply sent!");
        return "redirect:/owner/messages";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        model.addAttribute("user", getLoggedInUser(auth));
        return "owner/profile";
    }
}
