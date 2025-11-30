package com.eventaro.Eventaro.presentation.rest;

import com.eventaro.Eventaro.application.audit.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuditController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/log")
    public String showAuditLog(Model model) {
        // Lädt jetzt echte Daten aus der Datenbank!
        model.addAttribute("auditLogs", auditLogService.getAllLogs());
        return "admin/audit-log";
    }
}