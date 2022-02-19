package com.amigoscode.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRegistrationServiceTest {

    private CustomerRegistrationService customerServiceUnderTest;

    @BeforeEach
    void setUp() {
        customerServiceUnderTest = new CustomerRegistrationService();
    }
}