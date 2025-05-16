package com.ahm282.Akkoord.repository;

import com.ahm282.Akkoord.model.entity.AppUser;
import com.ahm282.Akkoord.model.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByFullName(String fullName);
    List<AppUser> findByDepartment(Department department);
}
