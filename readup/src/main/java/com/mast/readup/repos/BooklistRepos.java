package com.mast.readup.repos;

import com.mast.readup.entities.Booklist;
import com.mast.readup.entities.Utente;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BooklistRepos extends JpaRepository<Booklist, Long> {
    List<Booklist> findByUtenteCreatore(Utente utente);

}