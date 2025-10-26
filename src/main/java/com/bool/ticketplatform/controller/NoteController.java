package com.bool.ticketplatform.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bool.ticketplatform.model.Note;
import com.bool.ticketplatform.model.Ticket;
import com.bool.ticketplatform.model.User;
import com.bool.ticketplatform.repository.NoteRepository;
import com.bool.ticketplatform.repository.TicketRepository;
import com.bool.ticketplatform.repository.UserRepository;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;


    // creazione note
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("note") Note note, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {
        
        // binding error
        if (bindingResult.hasErrors()) {

            // Itera e stampa tutti gli errori
            System.err.println("Validation errors found:");
            for (ObjectError error : bindingResult.getAllErrors()) {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    System.err.println(
                            "  - Field: " + fieldError.getField() + ", Message: " + fieldError.getDefaultMessage());
                } else {
                    System.err.println("  - Global error: " + error.getDefaultMessage());
                }
            }
            Optional<Ticket> ticketOpt = ticketRepository.findById(note.getTicket().getId());
        
            if (!ticketOpt.isPresent()) {

                redirectAttributes.addFlashAttribute("erroreMessage", "ticket not found");
                return "redirect:/tickets/show/" + note.getTicket().getId();
            }
            model.addAttribute("ticket", ticketOpt.get());
            model.addAttribute("note", note);
            model.addAttribute("editMode", false);

            return "notes/edit"; 
        }

        // cerca ticket
        Optional<Ticket> ticketOpt = ticketRepository.findById(note.getTicket().getId());

        if (!ticketOpt.isPresent()) {

            redirectAttributes.addFlashAttribute("erroreMessage", "ticket not found");
            return "redirect:/tickets/show/" + note.getTicket().getId();
        }

        
        // cerco content uguali 
        Optional<Note> noteOptional = noteRepository.findByContent(note.getContent());
        if (noteOptional.isPresent()) {
            model.addAttribute("errorMessage", "content already exist");
            model.addAttribute("ticket", ticketOpt.get());
            model.addAttribute("note", note);
            model.addAttribute("editMode", false);
            return "notes/edit"; 
        }


        // cerco utente loggato
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> userOptional = userRepository.findByUsername(currentUsername);

        if (!userOptional.isPresent()) {
            return "redirect:/login";
        }
        
        // salva ticket
        Ticket ticket = ticketOpt.get();
        note.setTicket(ticket);
        note.setUser(userOptional.get());
        note.setDateCreation(LocalDateTime.now());
        model.addAttribute("editMode", false);
        noteRepository.save(note);

        return "redirect:/tickets/show/" + note.getTicket().getId();
    }
    
    
    // modifica note
    // get modifica note
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {

        // cerco note
        Optional<Note> noteOpt = noteRepository.findById(id);

        if (noteOpt.isPresent()) {
            model.addAttribute("note", noteOpt.get());
            model.addAttribute("editMode", true);
        }

        return "notes/edit";
    }

    // post modifica note
    @PostMapping("/edit/{id}")
    public String update(@PathVariable("id") Integer id, @Valid @ModelAttribute("note") Note noteForm, BindingResult bindingResult,
            Model model, RedirectAttributes redirectAttributes) {

        // binding error
        if (bindingResult.hasErrors()) {
            System.err.println("Validation errors found:");
            // Itera e stampa tutti gli errori
            for (ObjectError error : bindingResult.getAllErrors()) {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    System.err.println(
                            "  - Field: " + fieldError.getField() + ", Message: " + fieldError.getDefaultMessage());
                } else {
                    System.err.println("  - Global error: " + error.getDefaultMessage());
                }
            }

            model.addAttribute("note", noteForm);
            model.addAttribute("editMode", true);
            return "notes/edit";
        }

        // cerco nota
        Note oldNote = noteRepository.findById(id).get();
        
        // salvo nota nuova
        oldNote.setTicket(noteForm.getTicket());
        oldNote.setContent(noteForm.getContent());
        
        noteRepository.save(oldNote);
        redirectAttributes.addFlashAttribute("successMessage", "note updated successfully");

        return "redirect:/tickets/show/" + oldNote.getTicket().getId();
    }



    // cancella nota
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        
        // cerco nota
        Note note = noteRepository.findById(id).get();

        // cancello nota
        Ticket ticket = note.getTicket();
        noteRepository.delete(note);

        redirectAttributes.addFlashAttribute("successMessage", "Note delete with success!");
        return "redirect:/tickets/show/" + ticket.getId();
    }


}
