package com.esfera.g2.esferag2.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAddress;

    private String street;

    private String city;

    private String state;

    private String zipCode;

    private String number;

    private String country;

    @ManyToOne
    @JoinColumn(name = "Client_idClient")
    private Client client;

    public Long getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(Long idAddress) {
        this.idAddress = idAddress;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client idClient) {
        this.client = idClient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(idAddress, address.idAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAddress);
    }

    public Address() {
    }

    public Address(String zipCode, String country, String state, String city, String street, String number) {
        this.zipCode = zipCode;
        this.country = country;
        this.state = state;
        this.city = city;
        this.street = street;
        this.number = number;
    }
}
