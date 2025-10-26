package com.bool.ticketplatform.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import com.bool.ticketplatform.model.Role;
import com.bool.ticketplatform.model.Ticket;
import com.bool.ticketplatform.model.User;
import com.bool.ticketplatform.repository.RoleRepository;
import com.bool.ticketplatform.repository.StatusTicketRepository;
import com.bool.ticketplatform.repository.TicketRepository;
import com.bool.ticketplatform.repository.UserRepository;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private StatusTicketRepository statusTicketRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // get per pagina profilo
    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {

        // cerco utente
        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            return "redirect:/login";
        }

        User currentUser = userOptional.get();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("list", ticketRepository.findByUser_Username(username));
        model.addAttribute("statusList", statusTicketRepository.findAll());
        model.addAttribute("username", username);

        return "users/profile";
    }

    
    // post salva disponibilità 
    @PostMapping("/updateAvailability")
    public String updateAvailability(@ModelAttribute("currentUser") User userForm, Authentication authentication,
            Model model, RedirectAttributes redirectAttributes) {

        // cerco utente
        String username = authentication.getName();
        Optional<User> userToUpdateOptional = userRepository.findByUsername(username);

        if (!userToUpdateOptional.isPresent()) {

            redirectAttributes.addFlashAttribute("errorMessage", "ticket not found!");
            return "redirect:/login";
        }

        User userToUpdate = userToUpdateOptional.get();

        // cerco ticket
        List<Ticket> assignedTickets = ticketRepository.findByUser(userToUpdate);

        if (!assignedTickets.isEmpty() && !userForm.isAvailable()) {

            model.addAttribute("currentUser", userToUpdate);
            model.addAttribute("hasTickets", true);
            model.addAttribute("errorMessage", "Can't change status while have ticket assigned");
            model.addAttribute("list", ticketRepository.findByUser_Username(username));
            model.addAttribute("statusList", statusTicketRepository.findAll());
            model.addAttribute("username", username);

            return "tickets/index";
        }

        // salvo disponibilità
        userToUpdate.setAvailable(userForm.isAvailable());
        userRepository.save(userToUpdate);
        return "redirect:/tickets";
    }

    
    // get per index utente
    @GetMapping("/index")
    public String list(Model model) {

        model.addAttribute("usersList", userRepository.findAll());
        return "users/index";
    }

    // get crea utente
    @GetMapping("/formUser")
    public String createFormUser(Model model) {

        User newUser = new User();
        
        List<Role> roles = roleRepository.findAll();

        model.addAttribute("user", newUser);
        model.addAttribute("editMode", false);
        model.addAttribute("roleList", roles);

        return "users/formUser";
        
    }
    
    // post crea utente    
    @PostMapping("/formUser")
    public String save(@Valid @ModelAttribute("user") User formUser, BindingResult bindingResult,
            @RequestParam("rolesId") Integer rolesId,
            RedirectAttributes redirectAttributes, Model model) {

        // cerco utente
        Optional<User> userOptUsername = userRepository.findByUsername(formUser.getUsername());
        Optional<User> userOptEmail = userRepository.findByEmail(formUser.getEmail());

        if (userOptEmail.isPresent() || userOptUsername.isPresent()) {
            model.addAttribute("errorMessage", "user already exist");
            model.addAttribute("roleList", roleRepository.findAll());
            model.addAttribute("editMode", false);
            model.addAttribute("user", formUser);
            return "users/formUser";
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

            model.addAttribute("user", formUser);
            model.addAttribute("roleList", roleRepository.findAll());
            model.addAttribute("editMode", false);

            return "users/formUser";
        }

        Optional<Role> roleOpt = roleRepository.findById(rolesId);
        
        if (roleOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Role not found!");
            return "redirect:/users/index";
        }

        Role role = roleOpt.get();
        
        formUser.addRole(role);

        formUser.setPassword(passwordEncoder.encode(formUser.getPassword()));
        formUser.setAvailable(true);

        userRepository.save(formUser);
        
        redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
        return "redirect:/users/index";
    }




    // post cancellazione utente
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {

        // cerco utente
        Optional<User> userOpt = userRepository.findById(id);

        if (!userOpt.isPresent()) {

            redirectAttributes.addFlashAttribute("successMessage", "ticket not found!");
            return "redirect:/users/index";
        }

        User user = userOpt.get();

        // verifico che non ha ticket
        if (user.getTickets() != null && !user.getTickets().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Impossible delete user with ticket assigned!");
            return "redirect:/users/index";
        }
    
        // cancello utente
        userRepository.delete(user);

        redirectAttributes.addFlashAttribute("successMessage", "user deleted with success!");
        return "redirect:/users/index";
    }

}