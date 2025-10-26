package com.bool.ticketplatform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bool.ticketplatform.model.Category;


public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // cerco per nome
    public Optional <Category> findByName(String name);
}
