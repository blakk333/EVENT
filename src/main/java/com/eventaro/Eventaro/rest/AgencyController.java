package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.model.CorporateClient;
import com.eventaro.Eventaro.service.AgencyService;
import com.eventaro.Eventaro.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/agencies")
public class AgencyController {

    @Autowired
    private AgencyService agencyService;

    @Autowired
    private EventService eventService;

    @GetMapping("/manage")
    public String listAgencies(Model model) {
        model.addAttribute("clients", agencyService.getAllClients());
        model.addAttribute("contingents", agencyService.getAllContingents());
        return "management/agencies-list"; // Template s.u.
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("client", new CorporateClient());
        return "management/agency-form"; // Template s.u.
    }

    @PostMapping("/save")
    public String saveAgency(@ModelAttribute CorporateClient client) {
        agencyService.saveClient(client);
        return "redirect:/agencies/manage";
    }

    // WI #82: Kontingent anlegen Seite
    @GetMapping("/contingent/create")
    public String showContingentForm(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("clients", agencyService.getAllClients());
        return "management/contingent-form";
    }

    @PostMapping("/contingent/save")
    public String saveContingent(@RequestParam Long clientId,
                                 @RequestParam Long eventId,
                                 @RequestParam int count,
                                 @RequestParam String expiryDate) {

        eventService.getEventById(eventId).ifPresent(event -> {
            agencyService.reserveContingent(clientId, event, count, LocalDate.parse(expiryDate));
        });

        return "redirect:/agencies/manage";
    }
}