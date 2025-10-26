package com.bool.ticketplatform.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bool.ticketplatform.model.Category;
import com.bool.ticketplatform.model.Note;
import com.bool.ticketplatform.model.Role;
import com.bool.ticketplatform.model.StatusTicket;
import com.bool.ticketplatform.model.Ticket;
import com.bool.ticketplatform.model.User;
import com.bool.ticketplatform.repository.CategoryRepository;
import com.bool.ticketplatform.repository.NoteRepository;
import com.bool.ticketplatform.repository.StatusTicketRepository;
import com.bool.ticketplatform.repository.TicketRepository;
import com.bool.ticketplatform.repository.UserRepository;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatusTicketRepository statusTicketRepository;

    @Autowired
    private NoteRepository noteRepository;


    // get per index
    @GetMapping
    public String index(Model model, @RequestParam(name = "titleString", required = false) String titleString,
            @RequestParam(name = "categoryString", required = false) String categoryString,
            @RequestParam(name = "statusString", required = false) String statusString, Authentication authentication) {
        
        // cerco utente loggato
        List<Ticket> result = null;

        String username = authentication.getName();
        Optional<User> currentUserOptional = userRepository.findByUsername(username);

        if (!currentUserOptional.isPresent()) {

            return "redirect:/login";
        }
        
        User currentUser = currentUserOptional.get();
        boolean isAdmin = false;

        // ciclo per capire il tipo di ruolo
        for (Role role : currentUser.getRoles()) {
            if ("ADMIN".equals(role.getRoleName())) {
                isAdmin = true;
                break;
            }
        }

        // filtri 
        if (isAdmin) {

            if (titleString != null && !titleString.isBlank()) {
                result = ticketRepository.findByTitleContainingIgnoreCase(titleString);
            } else if (categoryString != null && !categoryString.isBlank()) {
                result = ticketRepository.findByCategory_nameContainingIgnoreCase(categoryString);
            } else if (statusString != null && !statusString.isBlank()) {
                result = ticketRepository.findByStatus_statusTypeContainingIgnoreCase(statusString);
            } else {
                result = ticketRepository.findAll();
            }
        } else {
                
                if (titleString != null && !titleString.isBlank()) {
                    result = ticketRepository.findByUser_UsernameAndTitleContainingIgnoreCase(username, titleString);
                } else if (categoryString != null && !categoryString.isBlank()) {
                    result = ticketRepository.findByUser_UsernameAndCategory_nameContainingIgnoreCase(username, categoryString);
                } else if (statusString != null && !statusString.isBlank()) {
                    result = ticketRepository.findByUser_UsernameAndStatus_statusTypeContainingIgnoreCase(username, statusString);
                } else {
                    result = ticketRepository.findByUser_Username(username);
                }

        }
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("hasTickets", !ticketRepository.findByUser(currentUser).isEmpty());
        model.addAttribute("username", username);
        model.addAttribute("list", result);
        model.addAttribute("statusList", statusTicketRepository.findAll());

        return "tickets/index";
    }
    

    

    // pagina show
    @GetMapping("/show/{id}")
    public String show(@PathVariable("id") Integer id, Model model) {

        // cerco ticket
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);

        if (optionalTicket.isPresent()) {

            model.addAttribute("ticket", optionalTicket.get());
            model.addAttribute("empty", false);
        } else {

            model.addAttribute("empty", true);
        }

        return "tickets/show";
    }



    // get per creo ticket
    @GetMapping("/formTicket")
    public String formTicket(Model model) {

        // cerco se gli utenti che sono disponibili
        boolean isAvailable = true;

        List<User> userListsAvailable = userRepository.findByAvailable(isAvailable);
        Ticket newTicket = new Ticket();

        model.addAttribute("categoriesList", categoryRepository.findAll());
        model.addAttribute("usersList", userListsAvailable);
        model.addAttribute("ticket", newTicket);
        model.addAttribute("editMode", false);

        return "tickets/formTicket";
    }

    // post per creare ticket
    @PostMapping("/formTicket")
    public String save(@Valid @ModelAttribute("ticket") Ticket formTicket, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Model model,
            @RequestParam("category.id") Integer categoryId, @RequestParam("user.id") Integer userId) {

        // cerco per lo stato
        Optional<StatusTicket> defaultStatusOpt = statusTicketRepository.findById(2);
        if (!defaultStatusOpt.isPresent()) {

            redirectAttributes.addFlashAttribute("errorMessage", "status ticket not found!");
            return "tickets/formTicket";
        }

        // cerco per la categoria
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (!categoryOptional.isPresent()) {

            redirectAttributes.addFlashAttribute("errorMessage", "category ticket not found!");
            return "tickets/formTicket";
        }
        // cerco per l utente       
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {

            redirectAttributes.addFlashAttribute("errorMessage", "user ticket not found!");
            return "tickets/formTicket";
        }
        
        // binding result
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

            // cerco per utenti disponibili
            boolean isAvailable = true;
            List<User> userListsAvailable = userRepository.findByAvailable(isAvailable);

            model.addAttribute("categoriesList", categoryRepository.findAll());
            model.addAttribute("usersList", userListsAvailable);
            model.addAttribute("editMode", false);
            return "tickets/formTicket";
        }

        // salvo ticket
        StatusTicket statusTicket = defaultStatusOpt.get();
        Category category = categoryOptional.get();
        User user = userOptional.get();
        formTicket.setCategory(category);
        formTicket.setUser(user);
        formTicket.setDateCreation(LocalDateTime.now());
        formTicket.setStatus(statusTicket);
        ticketRepository.save(formTicket);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket create with success!");

        return "redirect:/tickets";
    }



    // update stato
    @PostMapping("/updateStatus")
    public String updateTicketStatus(@RequestParam("ticketId") Integer ticketId,
            @RequestParam("statusId") Integer statusId, RedirectAttributes redirectAttributes) {

        // cerco ticket e stato
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        Optional<StatusTicket> newStatusOpt = statusTicketRepository.findById(statusId);

        if (!ticketOpt.isPresent() && !newStatusOpt.isPresent()) {

            redirectAttributes.addFlashAttribute("errorMessage", "impossible update status!");
            return "redirect:/tickets";
        }

        // salvo nuovo stato
        Ticket ticket = ticketOpt.get();
        StatusTicket newStatus = newStatusOpt.get();

        ticket.setStatus(newStatus);
        ticketRepository.save(ticket);

        return "redirect:/tickets";
    } 



    // get modifica ticket
    @GetMapping("/formTicket/{id}")
    public String edit(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

        // cerco ticket
        Optional<Ticket> optTicket = ticketRepository.findById(id);

        if (!optTicket.isPresent()) {

            redirectAttributes.addFlashAttribute("errorMessage", "ticket not found!");
            return "redirect:/tickets";
        }

        Ticket ticket = optTicket.get();

        model.addAttribute("ticket", ticket);
        model.addAttribute("categoriesList", categoryRepository.findAll());
        model.addAttribute("usersList", userRepository.findAll());
        model.addAttribute("editMode", true);

        return "tickets/formTicket";
    }

    // post per modifica ticket
    @PostMapping("/formTicket/{id}")
    public String update(@PathVariable("id") Integer ticketId, @Valid @ModelAttribute("ticket") Ticket formTicket,
            BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes,
            @RequestParam("category.id") Integer categoryId,
            @RequestParam("user.id") Integer userId,
            @RequestParam("status.id") Integer statusId) {

        // binding result
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

            model.addAttribute("categoriesList", categoryRepository.findAll());
            model.addAttribute("usersList", userRepository.findAll());
            model.addAttribute("editMode", true);
            return "tickets/formTicket";
        }

        // cerco ticket vecchio
        Optional<Ticket> oldTicketOptional = ticketRepository.findById(ticketId);
        if (!oldTicketOptional.isPresent()) {

            redirectAttributes.addFlashAttribute("errorMessage", "ticket not found!");
            return "redirect:/tickets";
        }

        // cerco per la categoria
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (!categoryOptional.isPresent()) {

            redirectAttributes.addFlashAttribute("errorMessage", "category ticket not found!");
            return "tickets/formTicket";
        }

        // cerco per l utente       
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {

            redirectAttributes.addFlashAttribute("errorMessage", "user ticket not found!");
            return "tickets/formTicket";
        }

        // cerco status   
        Optional<StatusTicket> statusTickeOptional = statusTicketRepository.findById(statusId);
        if (!userOptional.isPresent()) {

            redirectAttributes.addFlashAttribute("errorMessage", "status ticket not found!");
            return "tickets/formTicket";
        }

        // salvo ticket
        Category category = categoryOptional.get();
        User user = userOptional.get();
        StatusTicket statusTicket = statusTickeOptional.get();
        Ticket oldTicket = oldTicketOptional.get();

        oldTicket.setTitle(formTicket.getTitle());
        oldTicket.setDescription(formTicket.getDescription());
        oldTicket.setDateUpdate(LocalDateTime.now());
        oldTicket.setStatus(statusTicket);
        oldTicket.setCategory(category);
        oldTicket.setUser(user);

        ticketRepository.save(oldTicket);
        redirectAttributes.addFlashAttribute("successMessage", "ticket updated successfully");

        return "redirect:/tickets";
    }



    // cancella ticket
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

        // cerco ticket
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);

        if (!ticketOptional.isPresent()) {

            redirectAttributes.addFlashAttribute("errorMessage", "ticket not found!");
            return "redirect:/tickets";
        }

        Ticket ticket = ticketOptional.get();

        // ciclo per cancellare tutte le note associate
        for (Note noteToDelete : ticket.getNotes()) {

            noteRepository.delete(noteToDelete);
        }

        // cancella ticket
        ticketRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "ticket delete with success!");
        return "redirect:/tickets";
    }

    
    // get per le note
    @GetMapping("/{id}/notes")
    public String createNoteForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        
        // cerco ticket
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        
        if (!ticketOpt.isPresent()) {

            redirectAttributes.addFlashAttribute("errorMessage", "ticket not found!");
            return "redirect:/tickets";
        }
        
        Ticket ticket = ticketOpt.get(); 

        Note note = new Note();
        note.setTicket(ticket);
        model.addAttribute("note", note);
        model.addAttribute("editMode", false);
        
        return "notes/edit";
        
    }
}
