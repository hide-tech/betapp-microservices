package com.yazykov.sportbet.customerservice.controller;

import com.yazykov.sportbet.customerservice.dto.CreditCardApiDto;
import com.yazykov.sportbet.customerservice.dto.CustomerApiDto;
import com.yazykov.sportbet.customerservice.exception.CreditCardNotFoundException;
import com.yazykov.sportbet.customerservice.exception.CustomerAlreadyExistException;
import com.yazykov.sportbet.customerservice.exception.CustomerNotFoundException;
import com.yazykov.sportbet.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<?> getCustomerById(@PathVariable("customerId") Long customerId)
            throws CustomerNotFoundException {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    @GetMapping("/customers/email/{email}")
    public ResponseEntity<?> getCustomerById(@PathVariable("email") String email)
            throws CustomerNotFoundException {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    @PostMapping("/customers")
    public ResponseEntity<?> createNewCustomer(@RequestBody CustomerApiDto customerApiDto)
            throws CustomerAlreadyExistException {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createNewCustomer(customerApiDto));
    }

    @PatchMapping("/customers")
    public ResponseEntity<?> updateCustomer(@RequestBody CustomerApiDto customerApiDto)
            throws CustomerNotFoundException {
        return ResponseEntity.ok(customerService.updateCustomer(customerApiDto));
    }

    @PostMapping("/customers/{customerId}/cards")
    public ResponseEntity<?> addCreditCard(@RequestBody CreditCardApiDto creditCardApiDto,
                                           @PathVariable("customerId") Long customerId)
            throws CustomerNotFoundException {
        return ResponseEntity.ok(customerService.addCardToCustomer(creditCardApiDto, customerId));
    }

    @DeleteMapping("/customers/{customerId}/cards")
    public ResponseEntity<?> removeCreditCard(@RequestBody CreditCardApiDto creditCardApiDto,
                                              @PathVariable("customerId") Long customerId)
            throws CustomerNotFoundException, CreditCardNotFoundException {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(customerService
                .removeCard(creditCardApiDto, customerId));
    }

    @PostMapping("/customers/{customerId}/active")
    public ResponseEntity<?> activateCustomer(@PathVariable("customerId") Long customerId){
        customerService.activateCustomer(customerId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
