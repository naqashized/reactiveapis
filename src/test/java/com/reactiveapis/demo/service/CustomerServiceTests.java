package com.reactiveapis.demo.service;

import com.reactiveapis.demo.dto.CustomerDetailsResponse;
import com.reactiveapis.demo.dto.CustomerRequest;
import com.reactiveapis.demo.exceptions.InvalidCustomerException;
import com.reactiveapis.demo.model.Customer;
import com.reactiveapis.demo.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataR2dbcTest
@Testcontainers
public class CustomerServiceTests {
    @Container
    private static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>("postgres:latest");
    @Autowired
    private CustomerRepository customerRepository;

    private CustomerService customerService;

    @BeforeEach
    public void setup(){
        customerService = new CustomerService(customerRepository);
    }

    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> String.format(
                "r2dbc:postgresql://%s:%s/%s",
                postgresql.getHost(),
                postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
                postgresql.getDatabaseName())
        );
        registry.add("spring.r2dbc.username", postgresql::getUsername);
        registry.add("spring.r2dbc.password", postgresql::getPassword);

        registry.add("spring.flyway.url", postgresql::getJdbcUrl);
        registry.add("spring.flyway.user", postgresql::getUsername);
        registry.add("spring.flyway.password", postgresql::getPassword);
    }

    @Test
    public void shouldCreateCustomer(){
        var customerRequest = new CustomerRequest("Test", "+4911122222", "test@email.com", "");

        var response = customerService.create(customerRequest).block();
        assertTrue(response.id() != null);
    }

    @Test
    public void shouldNotCreateCustomer(){
        var customerRequest = new CustomerRequest(null, "+4911122222", "test@email.com", "");

        var response = customerService.create(customerRequest);
        StepVerifier.create(response).expectError(InvalidCustomerException.class).verify();
    }

    @Test
    public void shouldFindAllCustomers(){
        var customer = new Customer(UUID.randomUUID(), "Amazon", "", "test@email.com", "+4911122222", Instant.now());
        customerRepository.save(customer).block();

        var response = customerService.findAll().collectList().block();
        assertFalse(response.isEmpty());
        StepVerifier.create(Mono.just(response.get(0))).assertNext(result ->
            assertThat(result).usingRecursiveComparison().
                    isEqualTo(new CustomerDetailsResponse(customer.getId().toString(),
                    customer.getName(), customer.getPhone())
                    )
        ).verifyComplete();
    }
}
