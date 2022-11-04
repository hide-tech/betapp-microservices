package com.yazykov.sportbet.customerservice.mapper;

import com.yazykov.sportbet.customerservice.domain.Customer;
import com.yazykov.sportbet.customerservice.dto.CustomerApiDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer customerApiDtoToCustomer(CustomerApiDto customerApiDto);

    CustomerApiDto customerToCustomerApiDto(Customer customer);
}
