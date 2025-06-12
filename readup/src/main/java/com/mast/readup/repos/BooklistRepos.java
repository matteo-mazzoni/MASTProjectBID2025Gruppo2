package com.mast.readup.repos;

import com.mast.readup.entities.Booklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BooklistRepos extends JpaRepository<Booklist, Long> {
    
}