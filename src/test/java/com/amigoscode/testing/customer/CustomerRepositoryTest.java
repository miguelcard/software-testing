package com.amigoscode.testing.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // drops all tables before if exists and creates H2 DB and at end drops table again -> clean DB for tests
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepoUnderTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() { // TO BE WRITEN
        // Given
        UUID uuid = UUID.randomUUID();
        String phoneNumber = "6666";
        Customer customer = new Customer(uuid, "Patricio", phoneNumber);
        customerRepoUnderTest.save(customer);

        // When
        Optional<Customer> customerOptional = customerRepoUnderTest.selectCustomerByPhoneNumber(phoneNumber);

        // Then
        assertThat(customerOptional).isPresent().hasValueSatisfying(c -> assertThat(c).isEqualToComparingFieldByField(customer));
    }

    @Test
    void itShouldNotBeSelectCustomerByPhoneNumberIfNotExists() {
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
                    //or you can dos simplpy:
                    assertThat(c).isEqualToComparingFieldByField(customer);
                    //  USING ASSERTJ LIBRARY
                });
    }
}