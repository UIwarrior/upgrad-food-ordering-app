package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AddressService {
    @Autowired
    private CustomerAddressDao customerAddressDao;

    @Autowired
    private CustomerDao customerDao;


    @Autowired
    private AddressDao addressDao;

    @Autowired
    private StateDao stateDao;

    //Creating and persisting a new address added by customer
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(final AddressEntity addressEntity,final CustomerEntity customerEntity)throws SaveAddressException, AuthorizationFailedException{
        //pincode must have digits only from 0 to 9 and must be of 6 digits
        String pinCodeRegex ="^[0-9]{6}$";
        if(addressEntity.getFlatBuilNumber().isEmpty()
                || addressEntity.getLocality().isEmpty()
                || addressEntity.getCity().isEmpty()
                || addressEntity.getPinCode().isEmpty()
                || addressEntity.getUuid().isEmpty()){
            throw new SaveAddressException("SAR-001", "No field can be empty.");
        }
        //pincode must be of the proper format
        else if (!addressEntity.getPinCode().matches(pinCodeRegex)){
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }
        else {
            AddressEntity persistedAddressEntity = addressDao.createAddress(addressEntity);
            final CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
            customerAddressEntity.setAddress(persistedAddressEntity);
            customerAddressEntity.setCustomer(customerEntity);
            createCustomerAddress(customerAddressEntity);
            return persistedAddressEntity;
        }
    }

    //Get state details by UUID
    public StateEntity getStateByUUID(final String stateUuid) throws AddressNotFoundException{

        StateEntity stateEntity =stateDao.getStateByUuid(stateUuid);
        if(stateEntity == null){
            throw new AddressNotFoundException("ANF-002","No state by this id");
        }
        else {
            return stateEntity;
        }
    }


    //Validating pincode format - length is 6 and contains only numbers
    public String validatePincode(final String pinCode) throws SaveAddressException{
        boolean validPincode = true;
        System.out.println("pin code");
        System.out.println(pinCode);
        if(pinCode == null ){
            throw new SaveAddressException("SAR-002","Invalid pincode");
        }

        else if(pinCode.length() !=6 || pinCode.matches(".*[^0-9].*")){
                validPincode = false;
        }
        else {
            validPincode = true;
        }


        if(validPincode) {
            return pinCode;
        } else {
            throw new SaveAddressException("SAR-002","Invalid pincode");
        }
    }

    //Persisting created address entity
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity createCustomerAddress(final CustomerAddressEntity customerAddressEntity){
        return addressDao.createCustomerAddress(customerAddressEntity);
    }

    //Get all addresses for a customer entity
    @Transactional(propagation = Propagation.REQUIRED)
    public List<AddressEntity> getAllAddress (final CustomerEntity customerEntity){
        return customerAddressDao.getCustomerAddressListByCustomer(customerEntity);
    }

    //Getting an address by UUID
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity getAddressByUUID(final String addressUuid,final CustomerEntity customerEntity) throws AddressNotFoundException, AuthorizationFailedException {
        //if address id is empty, throw exception
        if(addressUuid.isEmpty()){
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }
        AddressEntity addressEntityToBeDeleted = addressDao.getAddressByAddressUuid(addressUuid);
        //if address id is incorrect and no such address exist in database
        if(addressEntityToBeDeleted == null){
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }
        else {
            final CustomerAddressEntity customerAddressEntity = getCustomerAddressByAddressId(addressEntityToBeDeleted);
            final CustomerEntity belongsToAddressEntity = customerAddressEntity.getCustomer();
            if(belongsToAddressEntity.getUuid() != customerEntity.getUuid()){
                throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address ");
            }
            else {
                return addressEntityToBeDeleted;
            }
        }
    }

    //Get Customer-Address entity
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity getCustomerAddressByAddressId(final AddressEntity addressEntity){
        return customerAddressDao.getCustomerAddressByAddressId(addressEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(AddressEntity addressEntity){
        return addressDao.deleteAddress(addressEntity);
    }

    //List all states available DB table
    @Transactional(propagation = Propagation.REQUIRED)
    public List<StateEntity> getAllStates(){
        return stateDao.getAllStates();
    }

}
