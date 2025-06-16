package com.mast.readup.repos;

import com.mast.readup.entities.Booklist;
import com.mast.readup.entities.Utente;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface BooklistRepos extends JpaRepository<Booklist, Long> {

    @Query("SELECT b FROM Booklist b WHERE b.idUtenteCreatore = idUtenteCreatore")
    List<Booklist> findByUtenteCreatore(Utente utente);

}