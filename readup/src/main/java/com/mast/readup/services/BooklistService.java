package com.mast.readup.services;

import com.mast.readup.entities.Booklist;
import com.mast.readup.entities.Libro;
import java.util.List;

public interface BooklistService {
    Booklist creaBooklist(Long idUtenteCreatore, String nomeBooklist, String nicknameCreatore);
    void eliminaBooklist(long idBooklist);
    List<Booklist> getAllBooklistsByUser(String nickname);
    Booklist addBookToBooklist(long idBooklist, long idLibro, long idUtente); // userId è per autorizzazione
    void removeBookFromBooklist(long idBooklist, long idLibro, long idUtente); // userId è per autorizzazione
    List<Libro> getBooksInBooklist(long idBooklist);

    Booklist findById(long idBooklist); 
}