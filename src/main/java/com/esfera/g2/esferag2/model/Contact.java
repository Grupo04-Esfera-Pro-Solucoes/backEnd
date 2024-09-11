package com.esfera.g2.esferag2.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContact;

    private String data;

    @ManyToOne
    @JoinColumn (name = "TypeContact_idTypeContact")
    private TypeContact idTypeContact;

    @ManyToOne
    @JoinColumn(name = "Client_idClient")
    private Client client;

    public Long getIdContact() {
        return idContact;
    }

    public void setIdContact(Long idContact) {
        this.idContact = idContact;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public TypeContact getIdTypeContact() {
        return idTypeContact;
    }

    public void setIdTypeContact(TypeContact idTypeContact) {
        this.idTypeContact = idTypeContact;
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
        Contact contact = (Contact) o;
        return Objects.equals(idContact, contact.idContact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idContact);
    }

    public Contact() {
    }

    public Contact(String data, TypeContact idTypeContact, Client client) {
        this.data = data;
        this.idTypeContact = idTypeContact;
        this.client = client;
    }
}
