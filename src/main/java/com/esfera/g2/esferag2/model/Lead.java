package com.esfera.g2.esferag2.model;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "leads")
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLead;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private java.sql.Timestamp date;

    @Column(nullable = false)
    private String duration;

    @Column
    private String callTime;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(nullable = false)
    private LeadResult result;

    @ManyToOne
    @JoinColumn(name = "Client_idClient")
    private Client idClient;

    public Lead(Long idLead, String contact, Timestamp date, String duration, String callTime, String description, LeadResult result, Client idClient) {
        this.idLead = idLead;
        this.contact = contact;
        this.date = date;
        this.duration = duration;
        this.callTime = callTime;
        this.description = description;
        this.result = result;
        this.idClient = idClient;
    }

    public Lead() {
    }

    public Long getIdLead() {
        return idLead;
    }

    public void setIdLead(Long idLead) {
        this.idLead = idLead;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Client getIdClient() {
        return idClient;
    }

    public void setIdClient(Client idClient) {
        this.idClient = idClient;
    }

    public LeadResult getResult() {
        return result;
    }

    public void setResult(LeadResult result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lead lead = (Lead) o;
        return Objects.equals(idLead, lead.idLead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLead);
    }
}
