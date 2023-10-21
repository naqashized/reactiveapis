package com.reactiveapis.demo.service;

import com.reactiveapis.demo.dto.CustomerDetailsResponse;
import com.reactiveapis.demo.dto.CustomerRequest;
import com.reactiveapis.demo.dto.CustomerResponse;
import com.reactiveapis.demo.exceptions.InvalidCustomerException;
import com.reactiveapis.demo.model.Customer;
import com.reactiveapis.demo.repository.CustomerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;


    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Mono<CustomerResponse> create(CustomerRequest customerRequest){
        var customer = new Customer(UUID.randomUUID(), customerRequest.name(),
                customerRequest.logo(), customerRequest.email(),
                customerRequest.phone(), Instant.now() );
        return customerRepository.save(customer).
                flatMap(newCustomer ->
                        Mono.just(new CustomerResponse(newCustomer.getId()))
                ).
                onErrorResume(DataIntegrityViolationException.class, ex ->
                        Mono.error(new InvalidCustomerException("Invalid customer data"))
                );
    }

    public Flux<CustomerDetailsResponse> findAll(){
        return customerRepository.findAll().flatMap(customer ->
                Mono.just(new CustomerDetailsResponse(customer.getId().toString(),
                customer.getName(), customer.getPhone())
                )
        );
    }
}
