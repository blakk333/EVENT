package com.eventaro.Eventaro.presentation.rest;

import com.eventaro.Eventaro.application.agency.AgencyService;
import com.eventaro.Eventaro.application.agency.dto.CorporateClientRequest;
import com.eventaro.Eventaro.application.event.EventService;
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
        return "management/agencies-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        // Wir nutzen hier das DTO für das Formular
        model.addAttribute("client", new CorporateClientRequest());
        return "management/agency-form";
    }

    @PostMapping("/save")
    public String saveAgency(@ModelAttribute("client") CorporateClientRequest clientRequest,
                             @RequestParam(required = false) Long id) {
        // Aufruf der neuen Service-Methode mit DTO und optionaler ID
        agencyService.createOrUpdateClient(clientRequest, id);
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
            // Hinweis: Hier müsste man saubererweise die Event-Entity laden, da reserveContingent eine Entity erwartet.
            // Da getEventById DTOs liefert, nutzen wir hier findEventEntityById aus dem Service (muss public sein)
            // oder mappen zurück.
            // Der Einfachheit halber: Wir holen die Entity direkt über den Service, falls vorhanden.
            eventService.findEventEntityById(eventId).ifPresent(eventEntity -> {
                agencyService.reserveContingent(clientId, eventEntity, count, LocalDate.parse(expiryDate));
            });
        });

        return "redirect:/agencies/manage";
    }
}