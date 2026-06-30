package com.hostel.management.controller;

import com.hostel.management.model.*;
import com.hostel.management.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/visitor")
public class VisitorController {

    @Autowired private UserService userService;
    @Autowired private HostelService hostelService;
    @Autowired private MessageService messageService;

    private User getLoggedInUser(Authentication auth) {
        return userService.findByEmailOrPhone(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User visitor = getLoggedInUser(auth);
        List<Hostel> allHostels = hostelService.getAllActiveHostels();
        List<SavedHostel> saved = hostelService.getSavedHostels(visitor);
        long unread = messageService.countUnreadMessages(visitor);

        model.addAttribute("visitor", visitor);
        model.addAttribute("hostels", allHostels.size() > 6 ? allHostels.subList(0, 6) : allHostels);
        model.addAttribute("savedCount", saved.size());
        model.addAttribute("unreadMessages", unread);
        model.addAttribute("hostelTypes", Hostel.HostelType.values());
        return "visitor/dashboard";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String location,
                         @RequestParam(required = false) String type,
                         Authentication auth,
                         Model model) {
        User visitor = getLoggedInUser(auth);
        Hostel.HostelType hostelType = null;
        if (type != null && !type.isEmpty()) {
            try { hostelType = Hostel.HostelType.valueOf(type); } catch (Exception ignored) {}
        }
        List<Hostel> results = hostelService.searchHostels(location, hostelType);

        model.addAttribute("hostels", results);
        model.addAttribute("visitor", visitor);
        model.addAttribute("searchLocation", location);
        model.addAttribute("searchType", type);
        model.addAttribute("hostelTypes", Hostel.HostelType.values());
        return "visitor/search-results";
    }

    @GetMapping("/hostel/{id}")
    public String viewHostel(@PathVariable Long id, Authentication auth, Model model) {
        User visitor = getLoggedInUser(auth);
        return hostelService.findById(id).map(hostel -> {
            model.addAttribute("hostel", hostel);
            model.addAttribute("visitor", visitor);
            model.addAttribute("reviews", hostelService.getHostelReviews(hostel));
            model.addAttribute("isSaved", hostelService.isSaved(visitor, hostel));
            model.addAttribute("hasReviewed", hostelService.hasReviewed(visitor, hostel));
            model.addAttribute("review", new Review());
            return "visitor/hostel-detail";
        }).orElse("redirect:/visitor/dashboard");
    }

    // ===== SAVE / UNSAVE =====

    @PostMapping("/hostel/{id}/save")
    public String saveHostel(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        User visitor = getLoggedInUser(auth);
        hostelService.findById(id).ifPresent(hostel -> hostelService.saveHostel(visitor, hostel));
        ra.addFlashAttribute("successMessage", "Hostel saved to your list!");
        return "redirect:/visitor/hostel/" + id;
    }

    @PostMapping("/hostel/{id}/unsave")
    public String unsaveHostel(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        User visitor = getLoggedInUser(auth);
        hostelService.findById(id).ifPresent(hostel -> hostelService.unsaveHostel(visitor, hostel));
        ra.addFlashAttribute("successMessage", "Hostel removed from your list.");
        return "redirect:/visitor/hostel/" + id;
    }

    @GetMapping("/saved")
    public String savedHostels(Authentication auth, Model model) {
        User visitor = getLoggedInUser(auth);
        model.addAttribute("savedHostels", hostelService.getSavedHostels(visitor));
        model.addAttribute("visitor", visitor);
        return "visitor/saved-hostels";
    }

    // ===== REVIEWS =====

    @PostMapping("/hostel/{id}/review")
    public String addReview(@PathVariable Long id,
                            @RequestParam(required = false) Integer rating,
                            @RequestParam String comment,
                            Authentication auth,
                            RedirectAttributes ra) {
        if (rating == null || rating < 1 || rating > 5) {
            ra.addFlashAttribute("errorMessage", "Please select a star rating between 1 and 5.");
            return "redirect:/visitor/hostel/" + id;
        }
        User visitor = getLoggedInUser(auth);
        hostelService.findById(id).ifPresent(hostel -> {
            if (!hostelService.hasReviewed(visitor, hostel)) {
                Review review = new Review();
                review.setRating(rating);
                review.setComment(comment);
                hostelService.addReview(review, visitor, hostel);
            }
        });
        ra.addFlashAttribute("successMessage", "Review submitted successfully!");
        return "redirect:/visitor/hostel/" + id;
    }

    // ===== MESSAGES =====

    @GetMapping("/messages")
    public String messages(Authentication auth, Model model) {
        User visitor = getLoggedInUser(auth);
        model.addAttribute("messages", messageService.getAllMessagesForUser(visitor));
        model.addAttribute("visitor", visitor);
        messageService.markAllAsReadForUser(visitor);
        return "visitor/messages";
    }

    @PostMapping("/hostel/{id}/contact")
    public String contactOwner(@PathVariable Long id,
                               @RequestParam String message,
                               Authentication auth,
                               RedirectAttributes ra) {
        User visitor = getLoggedInUser(auth);
        hostelService.findById(id).ifPresent(hostel -> {
            messageService.sendMessage(visitor, hostel.getOwner(), message, hostel);
        });
        ra.addFlashAttribute("successMessage", "Message sent to owner!");
        return "redirect:/visitor/hostel/" + id;
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        model.addAttribute("user", getLoggedInUser(auth));
        return "visitor/profile";
    }
}
