package com.oauthsession.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oauthsession.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Boolean existsByUsername(String username);

    Optional<Customer> findByUsername(String username);
}
