package com.mast.readup.services;

import com.mast.readup.entities.Booklist;
import com.mast.readup.repos.BooklistRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante per le transazioni

import java.util.List;
import java.util.Optional;

@Service // Indica a Spring che questa è una classe di servizio
public class BooklistServiceImpl implements BooklistService {

    @Autowired // Inietta il repository tramite il costruttore
    private final BooklistRepos booklistRepos;

    public BooklistServiceImpl(BooklistRepos booklistRepos) {
        this.booklistRepos = booklistRepos;
    }

    @Override
    @Transactional(readOnly = true) // Ottimizza per le operazioni di sola lettura
    public List<Booklist> getAll() {
        return booklistRepos.findAll();
    }

    @Override
    public Optional<Booklist> getById(Long idBooklist) {
        return booklistRepos.findById(idBooklist);
    }
}