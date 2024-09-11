package com.esfera.g2.esferag2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class TypeContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTypeContact;

    private String type;

    public TypeContact() {
    }

    public TypeContact(String type) {
        this.type = type;
    }

    public Long getIdTypeContact() {
        return idTypeContact;
    }

    public void setIdTypeContact(Long idTypeContact) {
        this.idTypeContact = idTypeContact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeContact that = (TypeContact) o;
        return Objects.equals(idTypeContact, that.idTypeContact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTypeContact);
    }
}
