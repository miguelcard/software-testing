package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.USD, Currency.GBP);

    private final PaymentRepository paymentRepo;
    private final CustomerRepository customerRepo;
    private final CardPaymentCharger paymentCharger;

    @Autowired
    public PaymentService(PaymentRepository paymentRepo, CustomerRepository customerRepo, CardPaymentCharger paymentCharger) { // this doesnt show error in amigos code
        this.paymentRepo = paymentRepo;
        this.customerRepo = customerRepo;
        this.paymentCharger = paymentCharger;
    }

    public void chargeCard(PaymentRequest request, UUID customerId) {
        // add logic and save payment
        //1. check customer exists, if not throw
        //2. do we support currency if not throw
        // 3. Charge card
        // 4. If not charged/debited throw
        // 5. Insert payment (db)
        // 6. TODO send sms

        Optional<Customer> customerOptional = customerRepo.findById(customerId);
        if (!customerOptional.isPresent()) {
            throw new IllegalStateException(String.format("User with id [%s] not found!", customerId));
        }
        Payment payment = request.getPayment();
        if(!ACCEPTED_CURRENCIES.stream().anyMatch(currency -> currency.equals(payment.getCurrency()))){
            throw new IllegalStateException(String.format("Currency not supported [%s] ", payment.getCurrency().toString()));
        }

        CardCharge charge = paymentCharger.chargeCard(
                payment.getAmount(),
                payment.getCurrency(),
                payment.getSource(),
                payment.getDescription()
        );

        if(!charge.isWasCharged()) {
            throw new IllegalStateException("Card was not charged!");
        }

        payment.setCustomerId(customerId);
        paymentRepo.save(payment);
    }
}
