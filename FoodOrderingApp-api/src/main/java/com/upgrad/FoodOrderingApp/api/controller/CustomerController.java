package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.LogoutResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;


@RestController
@CrossOrigin
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @RequestMapping(path = "/customer/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
            , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException{

        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID().toString());
        try {
            signupCustomerRequest.getFirstName().isEmpty();
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


    @RequestMapping(path = "/customer/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authentication") final String authentication) throws AuthenticationFailedException {
        byte[] decoded = null;
        String[] decodedArray=null;
        try {
            decoded = Base64.getDecoder().decode(authentication.split("Basic ")[1]);
        } catch (Exception e) {
            throw  new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
        String decodedText = new String(decoded);
        try{
            String temp=decodedText.split(":")[1];
            decodedArray = decodedText.split(":");

        } catch (Exception e){
            throw  new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
        final CustomerAuthEntity customerAuthToken = customerService.authenticate(decodedArray[0], decodedArray[1]);

        CustomerEntity customerEntity = customerAuthToken.getCustomer();

        LoginResponse loginResponse = new LoginResponse()
                .firstName(customerEntity.getFirstName())
                .lastName(customerEntity.getLastName())
                .emailAddress(customerEntity.getEmail())
                .contactNumber(customerEntity.getContactNum())
                .id(customerEntity.getUuid())
                .message("LOGGED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        List<String> header = new ArrayList<>();
        header.add("access-token");
        headers.setAccessControlExposeHeaders(header);
        headers.add("access-token", customerAuthToken.getAccessToken());

        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }


    @RequestMapping(path = "/customer/logout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader ("authorization") final String accessToken) throws AuthorizationFailedException {
        String[] bearerToken = accessToken.split("Bearer ");

        if(bearerToken.length==1){
            throw new AuthorizationFailedException("ATHR-005","Use valid authorization format <Bearer accessToken>");
        } else {
            final CustomerEntity customerAuthLogout = customerService.logout(bearerToken[1]);
            LogoutResponse logoutResponse = new LogoutResponse()
                    .id(customerAuthLogout.getUuid())
                    .message("LOGGED OUT SUCCESSFULLY");
            return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
        }
    }

}
