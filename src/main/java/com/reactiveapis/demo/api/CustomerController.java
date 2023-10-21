package com.reactiveapis.demo.api;

import com.reactiveapis.demo.dto.CustomerDetailsResponse;
import com.reactiveapis.demo.dto.CustomerRequest;
import com.reactiveapis.demo.dto.ErrorResponse;
import com.reactiveapis.demo.exceptions.InvalidCustomerException;
import com.reactiveapis.demo.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.reactiveapis.demo.dto.CustomResponse.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public Mono<ResponseEntity> create(@RequestBody CustomerRequest customerRequest){
        return customerService.create(customerRequest).flatMap(response -> ok(response)).
                onErrorResume(InvalidCustomerException.class, ex -> badRequest(ErrorResponse.create(ex)));
    }
    @GetMapping
    public Flux<CustomerDetailsResponse> findAll() {
        return customerService.findAll();
    }
}
