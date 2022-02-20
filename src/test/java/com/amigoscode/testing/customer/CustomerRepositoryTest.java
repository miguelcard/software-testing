package com.amigoscode.testing.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest( // drops all tables before if exists and creates H2 DB and at end drops table again -> clean DB for tests
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"  //Entity annotations will be treated when we run unit tests (e.g. NN constraints, unique constraints)
        }
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepoUnderTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given an existing customer
        UUID uuid = UUID.randomUUID();
        String phoneNumber = "6666";
        Customer customer = new Customer(uuid, "Patricio", phoneNumber);
        customerRepoUnderTest.save(customer);

        // When selected by phone number
        Optional<Customer> customerOptional = customerRepoUnderTest.selectCustomerByPhoneNumber(phoneNumber);

        // Then customer is found
        assertThat(customerOptional).isPresent().hasValueSatisfying(c -> assertThat(c).isEqualToComparingFieldByField(customer));
    }

    @Test
    void itShouldNotSelectCustomerByPhoneNumberIfNotExists() {
        // Given
        String nonExistentPhoneNumber = "000";
        // When
        Optional<Customer> customerOptional = customerRepoUnderTest.selectCustomerByPhoneNumber(nonExistentPhoneNumber);
        // Then
        assertThat(customerOptional).isNotPresent();
    }

    @Test
    void itShouldSaveCustomer() {
        // Given
        UUID uuid = UUID.randomUUID();
        Customer customer = new Customer(uuid, "Patricio", "6666");

        // When
        customerRepoUnderTest.save(customer);

        // Then
        Optional<Customer> customerOptional = customerRepoUnderTest.findById(uuid);
        assertEquals(true, customerOptional.isPresent()); // my test approach

        assertThat(customerOptional).isPresent().hasValueSatisfying( // amigoscode approach
                c -> {
//                    assertThat(c.getId()).isEqualTo(uuid);
//                    assertThat(c.getName()).isEqualTo("Patricio");
//                    assertThat(c.getPhoneNumber()).isEqualTo("6666");
                    //or you can do simply:
                    assertThat(c).isEqualToComparingFieldByField(customer);
                    //  USING ASSERTJ LIBRARY
                });
    }

    @Test
    void itShouldNotSaveCustomerWhenNullName() {
        // Given
        UUID uuid = UUID.randomUUID();
        Customer customer = new Customer(uuid, null, "6666");

        // When method save called
        // Then assert that correct exception and message are thrown
        assertThatThrownBy(() -> customerRepoUnderTest.save(customer))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value");
    }

    @Test
    void itShouldNotSaveCustomerWhenNullPhoneNumber() {
        // Given
        UUID uuid = UUID.randomUUID();
        Customer customer = new Customer(uuid, "Pepeee", null);

        // When method save called
        // Then assert that correct exception and message are thrown
        assertThatThrownBy(() -> customerRepoUnderTest.save(customer))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value");
    }
}