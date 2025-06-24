package com.mast.readup.repos;

import com.mast.readup.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UtenteRepos extends JpaRepository<Utente, Long> {

    // Derived query for form validation
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    
}
