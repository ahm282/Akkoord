package com.ahm282.Akkoord.repository;

import com.ahm282.Akkoord.model.entity.AppUser;
import com.ahm282.Akkoord.model.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    Optional<Department> findByName(String name);
}