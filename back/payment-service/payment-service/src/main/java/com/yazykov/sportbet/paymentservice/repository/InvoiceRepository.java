package com.yazykov.sportbet.paymentservice.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.yazykov.sportbet.paymentservice.domain.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InvoiceRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public Invoice save(Invoice invoice){
        dynamoDBMapper.save(invoice);
        return invoice;
    }

    public Invoice getById(String id){
        return dynamoDBMapper.load(Invoice.class, id);
    }
}
