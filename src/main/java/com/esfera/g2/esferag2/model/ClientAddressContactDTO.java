package com.esfera.g2.esferag2.model;

import java.util.List;

public class ClientAddressContactDTO {
    Client client;
    Address address;
    List<Contact> contact;

    public ClientAddressContactDTO() {
    }

    public ClientAddressContactDTO(Client client, Address address, List<Contact> contact) {
        this.client = client;
        this.address = address;
        this.contact = contact;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Contact> getContact() {
        return contact;
    }

    public void setContact(List<Contact> contact) {
        this.contact = contact;
    }
}
