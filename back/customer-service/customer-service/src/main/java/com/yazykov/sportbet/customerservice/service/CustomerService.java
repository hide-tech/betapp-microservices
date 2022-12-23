package com.yazykov.sportbet.customerservice.service;

import com.yazykov.sportbet.customerservice.config.AWSParametres;
import com.yazykov.sportbet.customerservice.domain.CreditCard;
import com.yazykov.sportbet.customerservice.domain.Customer;
import com.yazykov.sportbet.customerservice.domain.Status;
import com.yazykov.sportbet.customerservice.dto.CreditCardApiDto;
import com.yazykov.sportbet.customerservice.dto.CustomerApiDto;
import com.yazykov.sportbet.customerservice.dto.event.CustomerNotRegister;
import com.yazykov.sportbet.customerservice.dto.event.CustomerPaymentInfo;
import com.yazykov.sportbet.customerservice.dto.event.OrderInfo;
import com.yazykov.sportbet.customerservice.exception.CreditCardNotFoundException;
import com.yazykov.sportbet.customerservice.exception.CustomerAlreadyExistException;
import com.yazykov.sportbet.customerservice.exception.CustomerNotFoundException;
import com.yazykov.sportbet.customerservice.mapper.CreditCardMapper;
import com.yazykov.sportbet.customerservice.mapper.CustomerMapper;
import com.yazykov.sportbet.customerservice.repository.CreditCardRepository;
import com.yazykov.sportbet.customerservice.repository.CustomerRpository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRpository customerRpository;
    private final CustomerMapper customerMapper;
    private final CreditCardRepository creditCardRepository;
    private final CreditCardMapper creditCardMapper;
    private final QueueMessagingTemplate queueMessagingTemplate;
    private final AWSParametres parameters;

    public CustomerApiDto getCustomerById(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRpository.findById(customerId).orElseThrow(()->
                new CustomerNotFoundException("Customer doesn't exist"));
        return customerMapper.customerToCustomerApiDto(customer);
    }

    public CustomerApiDto getCustomerByEmail(String email) throws CustomerNotFoundException {
        Customer customer = customerRpository.findByEmail(email).orElseThrow(()->
                new CustomerNotFoundException("Customer doesn't exist"));
        return customerMapper.customerToCustomerApiDto(customer);
    }

    public CustomerApiDto createNewCustomer(CustomerApiDto customerApiDto) throws CustomerAlreadyExistException {
        Customer newCustomer = customerMapper.customerApiDtoToCustomer(customerApiDto);
        newCustomer.setStatus(Status.PENDING);
        Optional<Customer> check = customerRpository.findByEmail(customerApiDto.getEmail());
        if (check.isPresent()) throw new CustomerAlreadyExistException("Customer with this email already exist");
        Customer customer = customerRpository.save(newCustomer);
        return customerMapper.customerToCustomerApiDto(customer);
    }

    public CustomerApiDto updateCustomer(CustomerApiDto customerApiDto) throws CustomerNotFoundException {
        Customer customer = customerRpository.findById(customerApiDto.getId()).orElseThrow(()->
                new CustomerNotFoundException("Customer doesn't exist"));
        Customer newCustomer = setFields(customer, customerApiDto);
        Customer result = customerRpository.save(newCustomer);
        return customerMapper.customerToCustomerApiDto(result);
    }

    private Customer setFields(Customer customer, CustomerApiDto customerApiDto) {
        customer.setName(customerApiDto.getName());
        customer.setSurname(customerApiDto.getSurname());
        customer.setEmail(customerApiDto.getEmail());
        customer.setPhotoUrl(customerApiDto.getPhotoUrl());
        return customer;
    }

    public boolean addCardToCustomer(CreditCardApiDto creditCardApiDto, Long customerId)
            throws CustomerNotFoundException {
        CreditCard newCard = creditCardMapper.creditCardApiDtoToCreditCard(creditCardApiDto);
        CreditCard card = creditCardRepository.save(newCard);
        Customer customer = customerRpository.findById(customerId).orElseThrow(()->
                new CustomerNotFoundException("Customer doesn't exist"));
        List<CreditCard> cards = customer.getCards();
        if (cards==null) cards=new ArrayList<>();
        cards.add(card);
        customer.setCards(cards);
        customerRpository.save(customer);
        return true;
    }

    public boolean removeCard(CreditCardApiDto creditCardApiDto, Long customerId)
            throws CustomerNotFoundException, CreditCardNotFoundException {
        CreditCard card = creditCardRepository.findById(creditCardApiDto.getId()).orElseThrow(()->
                new CreditCardNotFoundException("Card doesn't exist"));
        Customer customer = customerRpository.findById(customerId).orElseThrow(()->
                new CustomerNotFoundException("Customer doesn't exist"));
        List<CreditCard> cards = customer.getCards();
        cards.remove(card);
        customer.setCards(cards);
        customerRpository.save(customer);
        return true;
    }

    public void activateCustomer(Long customerId){
        Customer customer = customerRpository.findById(customerId).get();
        customer.setStatus(Status.ACTIVE);
        customerRpository.save(customer);
    }

    public void process(OrderInfo orderInfo){
        boolean fault = false;
        String reason = "";
        Optional<Customer> opt = customerRpository.findById(orderInfo.getCustomerId());
        if (opt.isEmpty()) {
            fault = true;
            reason = "customer with id not found";
        }
        Customer customer = opt.get();
        if (!customer.getStatus().equals(Status.ACTIVE)) {
            fault = true;
            reason = "customer has no ACTIVE status";
        }

        Optional<CreditCard> cardOpt = creditCardRepository.findById(orderInfo.getCardId());
        if (cardOpt.isEmpty()) {
            fault = true;
            reason = "customer's card with id not found";
        }
        CreditCard card = cardOpt.get();

        if (fault){
            CustomerNotRegister cnr = new CustomerNotRegister(orderInfo.getCustomerId(),
                    orderInfo.getOrderId(), reason);
            Message<CustomerNotRegister> payload = MessageBuilder.withPayload(cnr)
                    .setHeader("message-group-id", "OrderQueue")
                    .setHeader("message-deduplication-id", orderInfo.getOrderId()+"customer")
                    .build();
            queueMessagingTemplate.send(parameters.distinctQueue(), payload);
        } else {
            CustomerPaymentInfo cpi = new CustomerPaymentInfo(orderInfo.getOrderId(), orderInfo.getCustomerId(),
                    creditCardMapper.creditCardToCreditCardApiDto(card));
            Message<CustomerPaymentInfo> message = MessageBuilder.withPayload(cpi)
                    .setHeader("message-group-id", "OrderQueue")
                    .setHeader("message-deduplication-id", orderInfo.getOrderId()+"customer")
                    .build();
            queueMessagingTemplate.send(parameters.distinctQueue(), message);
        }
    }
}
