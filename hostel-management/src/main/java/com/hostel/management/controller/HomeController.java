package com.hostel.management.controller;

import com.hostel.management.model.Hostel;
import com.hostel.management.service.HostelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private HostelService hostelService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<Hostel> featuredHostels = hostelService.getAllActiveHostels();
        // Show only top 6 on home page
        if (featuredHostels.size() > 6) {
            featuredHostels = featuredHostels.subList(0, 6);
        }
        model.addAttribute("featuredHostels", featuredHostels);
        return "home";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String location,
                         @RequestParam(required = false) String type,
                         Model model) {
        Hostel.HostelType hostelType = null;
        if (type != null && !type.isEmpty()) {
            try { hostelType = Hostel.HostelType.valueOf(type); } catch (Exception ignored) {}
        }
        List<Hostel> results = hostelService.searchHostels(location, hostelType);
        model.addAttribute("hostels", results);
        model.addAttribute("searchLocation", location);
        model.addAttribute("searchType", type);
        model.addAttribute("hostelTypes", Hostel.HostelType.values());
        return "search-results";
    }

    @GetMapping("/hostel/view/{id}")
    public String viewHostel(@PathVariable Long id, Model model) {
        return hostelService.findById(id).map(hostel -> {
            model.addAttribute("hostel", hostel);
            model.addAttribute("reviews", hostelService.getHostelReviews(hostel));
            return "hostel-detail";
        }).orElse("redirect:/home");
    }
}
