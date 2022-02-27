package com.amigoscode.testing.customer;

import com.amigoscode.testing.utils.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private PhoneNumberValidator phoneValidator;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor; // an argument captor "captures" the argument that is passed to a mock method

//    @Autowired  amigoscode didnt include it but same result
    private CustomerRegistrationService customerServiceUnderTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        customerServiceUnderTest = new CustomerRegistrationService(customerRepo, phoneValidator);
    }

    @Test
    void itShouldSaveNewCustomer() {
        // Given a customer and a phone number
        UUID id = UUID.randomUUID();
        String phoneNumber = "123";
        Customer customer = new Customer(id, "Pepo", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // mock that there was no customer with the given phone number so that is created new
        given(customerRepo.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        given(phoneValidator.test(any())).willReturn(true);

        // When
        customerServiceUnderTest.registerNewCustomer(request);

        // Then
        then(customerRepo).should().save(customerArgumentCaptor.capture()); // this object captures the customer object from the method of service
        Customer customerCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerCaptorValue).isEqualToComparingFieldByField(customer);
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        // Given a customer and a phone number
        String phoneNumber = "123";
        Customer customer = new Customer(null, "Pepo", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // mock that there was no customer with the given phone number so that is created new
        given(customerRepo.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        given(phoneValidator.test(any())).willReturn(true);

        // When
        customerServiceUnderTest.registerNewCustomer(request);

        // Then
        then(customerRepo).should().save(customerArgumentCaptor.capture()); // this object captures the customer object from the method of service
        Customer customerCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerCaptorValue).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(customerCaptorValue.getId()).isNotNull();
    }

    @Test
    void itShouldNotSaveExistingCustomerIfPhoneExistsAndSameName() {
        // Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "123";
        Customer customer = new Customer(id, "Pepo", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        // mock that there is a customer with given phone number
        given(customerRepo.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));
        given(phoneValidator.test(any())).willReturn(true);

        // When
        customerServiceUnderTest.registerNewCustomer(request);

        // Then
        then(customerRepo).should(never()).save(any()); // shouldnt call save
        // OR
        then(customerRepo).should().selectCustomerByPhoneNumber(phoneNumber); // or should call this one method and no more
        then(customerRepo).shouldHaveNoMoreInteractions();
    }

    @Test
    void itShouldThrowExceptionIfSameNumberNewCustomer() {
        // Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "123";
        Customer customer = new Customer(id, "Pepo", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        // mock that there is a different customer with same phone number
        Customer customer2 = new Customer(id, "Different Name", phoneNumber);
        given(customerRepo.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer2));
        given(phoneValidator.test(any())).willReturn(true);

        // When
        // Then
        // assert that exception is thrown
        // assert message is correctly thrown
        // assert that we dont call customerRepo.save()

        assertThatThrownBy(() -> customerServiceUnderTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Phone number [%s] already taken!", phoneNumber));

        then(customerRepo).should(never()).save(any());
    }

    @Test
    void itShouldThrowExceptionWhenPhoneNumberInvalid() {
        // Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "123";
        Customer customer = new Customer(id, "Pepo", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(phoneValidator.test(any())).willReturn(false);

        // When // Then
        assertThatThrownBy(() -> customerServiceUnderTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("the phone number %s is invalid", phoneNumber));
        then(customerRepo).shouldHaveNoInteractions();
    }
}