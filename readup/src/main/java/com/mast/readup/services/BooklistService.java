package com.mast.readup.services;

import com.mast.readup.entities.Booklist;

import java.util.List;
import java.util.Optional;

public interface BooklistService {
    Optional<Booklist> getById(Long idBooklist);
    List<Booklist> getAll();
    List<Booklist> getByIdUtenteCreatore(Long idUtenteCreatore);
    Booklist creaBooklist(Long idUtenteCreatore, String nome, String descrizione);
    Booklist modificaDatiBooklist(long idBooklist, String nuovoNome, String nuovaDescrizione);
    Booklist modificaLibriBooklist(long idBooklist, List<String> nuovaListaLibri);
    void eliminaBooklist(long idBooklist);
}