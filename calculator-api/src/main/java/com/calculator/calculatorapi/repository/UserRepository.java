package com.calculator.calculatorapi.repository;

import com.calculator.calculatorapi.models.User;
import com.calculator.calculatorapi.models.UserStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserById(UUID id);

    Optional<User> findUserByEmail(String email);

    Page<User> findAllByStatus(UserStatus userStatus, PageRequest pageRequest);

    boolean existsUserByEmail(String email);
}
