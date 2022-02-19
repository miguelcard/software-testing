package com.amigoscode.testing.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepo;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        // 1. PhoneNumber is taken?
        final String phoneNumber = request.getCustomer().getPhoneNumber();
        Optional<Customer> customerOptional = customerRepo.selectCustomerByPhoneNumber(phoneNumber);
        if(customerOptional.isPresent()) {
            // - 1.1 if taken and belongs to same customer (saving customer twice)  -> return customer
            Customer customer = customerOptional.get();
            if(customer.getName().equals(request.getCustomer().getName())){
//                return customer; ? or customerRepo.save(customer);
                return; //just the customer is already registered
            }
                // - 1.2 if no same customer -> throw exception
                throw new IllegalStateException(String.format("Phone number [s%] already taken!", phoneNumber));
        }
        // 2. else Save customer
        customerRepo.save(request.getCustomer());
    }
}
