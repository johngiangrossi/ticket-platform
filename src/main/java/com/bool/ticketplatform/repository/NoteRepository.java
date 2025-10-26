package com.bool.ticketplatform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bool.ticketplatform.model.Note;

public interface NoteRepository extends JpaRepository<Note, Integer> {

    public Optional <Note> findByContent(String content);
}

