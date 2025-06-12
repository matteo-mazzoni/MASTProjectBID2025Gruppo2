package com.mast.readup.repos;

import com.mast.readup.entities.Sfida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SfidaRepos extends JpaRepository<Sfida, Long> {

}
