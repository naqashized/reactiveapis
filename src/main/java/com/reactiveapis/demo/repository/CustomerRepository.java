package com.reactiveapis.demo.repository;

import com.reactiveapis.demo.model.Customer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface CustomerRepository extends R2dbcRepository<Customer, UUID> {
}
