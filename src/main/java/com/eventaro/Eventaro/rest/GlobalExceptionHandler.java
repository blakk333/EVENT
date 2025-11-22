package com.eventaro.Eventaro.rest;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Fängt EntityNotFoundException ab (z.B. Event ID gibts nicht)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFound(EntityNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error"; // Zeigt templates/error.html
    }

    // Fängt alle anderen unerwarteten Fehler ab
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, WebRequest request, Model model) {
        model.addAttribute("errorTitle", "An unexpected error occurred");
        model.addAttribute("errorMessage", ex.getMessage()); // In Prod evtl. verstecken
        return "error";
    }
}