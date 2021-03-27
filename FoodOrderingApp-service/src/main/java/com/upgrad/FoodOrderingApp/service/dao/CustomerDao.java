package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;

    //new customer creation
    public CustomerEntity createCustomer(CustomerEntity customerEntity){
        this.entityManager.persist(customerEntity);
        return customerEntity;
    }

    //Returns a customer data from contact number
    public CustomerEntity getCustomerByContactNum(String contactNum){
        try{
            return (CustomerEntity)this.entityManager.createNamedQuery("customerByContactNum", CustomerEntity.class).setParameter("contactNum", contactNum).getSingleResult();
        }catch(NoResultException nre){
            return null;
        }
    }

}
