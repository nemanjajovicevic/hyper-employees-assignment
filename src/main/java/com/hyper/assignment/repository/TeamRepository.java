package com.hyper.assignment.repository;

import com.hyper.assignment.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

    Optional<TeamEntity> findByName(String name);
}
