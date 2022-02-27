package com.amigoscode.testing.customer;

import com.amigoscode.testing.utils.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepo;
    private final PhoneNumberValidator phoneValidator;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepo, PhoneNumberValidator phoneValidator) {
        this.customerRepo = customerRepo;
        this.phoneValidator = phoneValidator;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        // 1. PhoneNumber is taken?
        String phoneNumber = request.getCustomer().getPhoneNumber();

        // Validate phone number
        if(!phoneValidator.test(phoneNumber)) {
            throw new IllegalStateException(String.format("the phone number %s is invalid", phoneNumber));
        }

        Optional<Customer> customerOptional = customerRepo.selectCustomerByPhoneNumber(phoneNumber);
        if(customerOptional.isPresent()) {
            // - 1.1 if taken and belongs to same customer (saving customer twice)  -> return customer
            Customer customer = customerOptional.get();
            if(customer.getName().equals(request.getCustomer().getName())){
//                return customer; ? or customerRepo.save(customer);
                return; //just the customer is already registered
            }
                // - 1.2 if no same customer -> throw exception
                throw new IllegalStateException(String.format("Phone number [%s] already taken!", phoneNumber));
        }

        if(request.getCustomer().getId() == null) {
            request.getCustomer().setId(UUID.randomUUID());
        }
        // 2. else Save customer
        customerRepo.save(request.getCustomer());
    }
}
