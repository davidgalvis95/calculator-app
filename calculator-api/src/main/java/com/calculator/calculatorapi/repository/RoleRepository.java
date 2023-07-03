package com.calculator.calculatorapi.repository;

import com.calculator.calculatorapi.models.Role;
import com.calculator.calculatorapi.models.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleByType(RoleType roleType);
}
