package com.esfera.g2.esferag2.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class  StatusProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStatusProposal;

    @Column(nullable = false)
    private String name;

    public StatusProposal() {
    }

    public StatusProposal(String name) {
        this.name = name;
    }

    public Long getIdStatusProposal() {
        return idStatusProposal;
    }

    public void setIdStatusProposal(Long idStatusProposal) {
        this.idStatusProposal = idStatusProposal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusProposal that = (StatusProposal) o;
        return Objects.equals(idStatusProposal, that.idStatusProposal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idStatusProposal);
    }
}
