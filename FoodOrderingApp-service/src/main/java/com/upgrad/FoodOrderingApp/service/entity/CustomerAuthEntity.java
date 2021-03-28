package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

/*Defining the CustomerAuth Entity based on customer_auth table*/
@Entity
@Table(name = "customer_auth")

/*Fetch customer based on the access token*/
@NamedQueries(
        @NamedQuery(name="customerAuthTokenByAccessToken", query = "select ct from CustomerAuthEntity ct where ct.accessToken = :accessToken")
)
public class CustomerAuthEntity implements Serializable {

    /*The primary key of the customer_auth table*/
    @Id
    @Column(
            name = "ID"
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer id;
    /*Must be unique and not null*/
    @Column(
            name = "UUID"
    )

    @NotNull
    @Size(
            max = 200
    )
    private String uuid;

    @ManyToOne
    @OnDelete(
            action = OnDeleteAction.CASCADE
    )
    /*Join with the customer table, acting as a foreign key to customer_auth table*/
    @JoinColumn(
            name = "CUSTOMER_ID"
    )
    private CustomerEntity customer;

    @Column(
            name = "ACCESS_TOKEN"
    )
    @NotNull
    @Size(
            max = 500
    )
    private String accessToken;

    @Column(
            name = "LOGIN_AT"
    )
    @NotNull
    private ZonedDateTime loginAt;

    @Column(
            name = "EXPIRES_AT"
    )
    @NotNull
    private ZonedDateTime expiresAt;

    @Column(
            name = "LOGOUT_AT"
    )
    private ZonedDateTime logoutAt;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ZonedDateTime getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(ZonedDateTime loginAt) {
        this.loginAt = loginAt;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public ZonedDateTime getLogoutAt() {
        return logoutAt;
    }

    public void setLogoutAt(ZonedDateTime logoutAt) {
        this.logoutAt = logoutAt;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
