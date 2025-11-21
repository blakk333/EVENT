package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.service.AuditLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/log")
    public String viewLogs(Model model) {
        model.addAttribute("logs", auditLogService.getAllLogs());
        return "view-audit-log/view-audit-log";
    }
}