package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.persistence.OrganizerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/organizers")
public class OrganizerViewController {

    private final OrganizerRepository organizerRepository;

    public OrganizerViewController(OrganizerRepository organizerRepository) {
        this.organizerRepository = organizerRepository;
    }

    @GetMapping
    public String viewOrganizers(Model model) {
        // Alle Organisatoren laden
        model.addAttribute("organizers", organizerRepository.findAll());
        return "view-organizers"; // Sucht nach view-organizers.html
    }
}