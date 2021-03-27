package com.upgrad.FoodOrderingApp.service.businness;


import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.regex.Pattern;



@Service
public class CustomerService {

    PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    private CustomerDao customerDao;

    //Creates a new customer after performing checks on the fields
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(final CustomerEntity customerEntity) throws SignUpRestrictedException {

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@"+
                "(?:[a-zA-Z0-9-]+\\.)+[a-z"+
                "A-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);

        String contactNumRegex = "^[0-9]{10}$";

        //If the contact number already exists in the database
        if(customerDao.getCustomerByContactNum(customerEntity.getContactNum()) != null){
            throw new SignUpRestrictedException("SGR-001","This contact number is already registered! Try other contact number.");
        }
        //if any other field other than last name is null
        else if (customerEntity.getFirstName().isEmpty() ||
                customerEntity.getContactNum().isEmpty() ||
                customerEntity.getEmail().isEmpty() ||
                customerEntity.getPassword().isEmpty()){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        //If the email ID provided by the customer is not in the correct format
        else if (!pattern.matcher(customerEntity.getEmail()).matches()){
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }
        //If the contact number provided by the customer is not in correct format
        else if (!customerEntity.getContactNum().matches(contactNumRegex)) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }
        //If the password provided by the customer is weak
        else {
            String password = customerEntity.getPassword();
            String[] encryptedText = this.passwordCryptographyProvider.encrypt(password);
            customerEntity.setSalt(encryptedText[0]);
            customerEntity.setPassword(encryptedText[1]);
            return this.customerDao.createCustomer(customerEntity);
        }
    }

}
