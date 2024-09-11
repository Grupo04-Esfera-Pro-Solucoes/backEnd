package com.esfera.g2.esferag2.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class LeadResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLeadResult;

    @Column(nullable = false)
    private String result;

    public LeadResult() {
    }

    public LeadResult(String result) {
        this.result = result;
    }

    public Long getIdLeadResult() {
        return idLeadResult;
    }

    public void setIdLeadResult(Long idLeadResult) {
        this.idLeadResult = idLeadResult;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeadResult that = (LeadResult) o;
        return Objects.equals(idLeadResult, that.idLeadResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLeadResult);
    }
}
