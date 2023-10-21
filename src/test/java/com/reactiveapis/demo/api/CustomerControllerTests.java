package com.reactiveapis.demo.api;

import com.reactiveapis.demo.dto.CustomerDetailsResponse;
import com.reactiveapis.demo.dto.CustomerRequest;
import com.reactiveapis.demo.dto.ErrorResponse;
import com.reactiveapis.demo.exceptions.InvalidCustomerException;
import com.reactiveapis.demo.service.CustomerService;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;

import static io.restassured.RestAssured.given;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,

        properties = {
                "spring.main.allow-bean-definition-overriding=true"
        }
)
@Testcontainers
public class CustomerControllerTests {
        @Container
        private static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>("postgres:latest");
        @Autowired
        private CustomerService customerService;

        @LocalServerPort
        private int port;

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
                // Arrange
                var payload = new CustomerRequest("Test", "+3999399333","test@gmail.com","");

                var request = new RequestSpecBuilder().setPort(this.port).build();

                //Act
                var validatableResponse =  given(request)
                        .when()
                        .contentType(ContentType.JSON)
                        .body(payload)
                        .post("/customers")
                        .then();

                //Assert
                validatableResponse.statusCode(HttpStatus.OK.value());
        }

        @Test
        public void shouldNotCreateCustomer(){
                // Arrange
                String invalidName = null;
                var invalidPayload = new CustomerRequest(invalidName, "+3999399333","test@gmail.com","");

                var request = new RequestSpecBuilder().setPort(this.port).build();

                //Act
                var validatableResponse =  given(request)
                        .when()
                        .contentType(ContentType.JSON)
                        .body(invalidPayload)
                        .post("/customers")
                        .then();

                //Assert
                validatableResponse.statusCode(HttpStatus.BAD_REQUEST.value());
                var response = validatableResponse.extract().as(ErrorResponse.class);
                var exception = new InvalidCustomerException(response.getMessage());
                assertThat(response).usingRecursiveComparison().isEqualTo(ErrorResponse.create(exception));
        }

        @Test
        public void shouldFindAllCustomers(){
                // Arrange
                var payload = new CustomerRequest("Test", "+3999399333","test@gmail.com","");
                customerService.create(payload).block();
                var request = new RequestSpecBuilder().setPort(this.port).build();

                //Act
                var validatableResponse =  given(request)
                        .when()
                        .contentType(ContentType.JSON)
                        .body(payload)
                        .get("/customers")
                        .then();

                //Assert
                validatableResponse.statusCode(HttpStatus.OK.value());
                JsonPath jsonResponse = validatableResponse.extract().jsonPath();
                var customers = (List<CustomerDetailsResponse>)jsonResponse.get();
                Assertions.assertFalse(customers.isEmpty());
                Assertions.assertTrue( customers.size() > 0);
        }
}
