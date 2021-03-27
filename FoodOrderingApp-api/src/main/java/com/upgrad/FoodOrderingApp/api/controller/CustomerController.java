package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

   @RequestMapping(path="/customer/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {

       final CustomerEntity customerEntity = new CustomerEntity();
       customerEntity.setUuid(UUID.randomUUID().toString());
       try { signupCustomerRequest.getFirstName().isEmpty();
           signupCustomerRequest.getContactNumber().isEmpty();
           signupCustomerRequest.getEmailAddress().isEmpty();
           signupCustomerRequest.getPassword().isEmpty();
       } catch ( Exception e) {
           throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
       }
       customerEntity.setFirstName(signupCustomerRequest.getFirstName());
       customerEntity.setLastName(signupCustomerRequest.getLastName());
       customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
       customerEntity.setContactNum(signupCustomerRequest.getContactNumber());
       customerEntity.setSalt("1234abc");
       customerEntity.setPassword(signupCustomerRequest.getPassword());

       final CustomerEntity newCustomerEntity = customerService.saveCustomer(customerEntity);

       SignupCustomerResponse customerResponse = new SignupCustomerResponse()
               .id(newCustomerEntity.getUuid())
               .status("CUSTOMER SUCCESSFULLY REGISTERED");
       return new ResponseEntity<SignupCustomerResponse>(customerResponse, HttpStatus.CREATED);
    }

}
