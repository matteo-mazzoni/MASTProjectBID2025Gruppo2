package com.mast.readup.services;

import com.mast.readup.entities.Booklist;

import java.util.List;
import java.util.Optional;

public interface BooklistService {
    Optional<Booklist> getById(Long idBooklist);
    List<Booklist> getAll();
}