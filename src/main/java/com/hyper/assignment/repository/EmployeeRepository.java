package com.hyper.assignment.repository;

import com.hyper.assignment.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {

    Optional<EmployeeEntity> findByResourceId(UUID resourceId);

    boolean existsByResourceId(UUID resourceId);

    List<EmployeeEntity> findByNameContainingIgnoreCase(String name);

    List<EmployeeEntity> findByResourceIdAndNameContainingIgnoreCase(UUID resourceId, String name);
}
