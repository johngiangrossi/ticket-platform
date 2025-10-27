package com.bool.ticketplatform.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.bool.ticketplatform.model.Category;
import com.bool.ticketplatform.repository.CategoryRepository;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;
  

    // get per index categorie
    @GetMapping("/index")
    public String index(Model model) {
                
        List<Category> list = categoryRepository.findAll();
        model.addAttribute("list", list);
        model.addAttribute("categoryObj", new Category());

        return "categories/index";
    }


    // post creazione categorie
    @PostMapping("/index")
    public String save(@Valid @ModelAttribute("categoryObj") Category category, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Model model) {

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

            List<Category> list = categoryRepository.findAll();
            model.addAttribute("list", list);
            return "categories/index";

        }

        try {

            // cerco categorie
            Optional<Category> categoryOpt = categoryRepository.findByName(category.getName());

            if (categoryOpt.isPresent()) {

                throw new IllegalArgumentException("category already exist");

            }

            // salvo categoria
            categoryRepository.save(category);
            redirectAttributes.addFlashAttribute("successMessage", "Category created successfully!");
            return "redirect:/categories/index";
            
        } catch (IllegalArgumentException e) {
            
            model.addAttribute("errorMessage", e.getMessage());

            List<Category> list = categoryRepository.findAll();
            model.addAttribute("list", list);
            return "categories/index";

        }
    }
    

    // cancellazione categoria
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Model model) {

        try {
    
            // cerco categoria
            Optional<Category> catOptional = categoryRepository.findById(id);

            if (!catOptional.isPresent()) {

                throw new NoSuchElementException("category is not found");
                
            }
            
            Category cat = catOptional.get();

            // se non ha ticket non cancello
            if (!cat.getTickets().isEmpty()) {

                throw new IllegalArgumentException("impossible delete a category with ticket associated");

            }

            // cancello categoria
            categoryRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deletes successfully!");
            
            return "redirect:/categories/index";
        
        } catch (NoSuchElementException | IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/categories/index";
                
        }
    }

}
