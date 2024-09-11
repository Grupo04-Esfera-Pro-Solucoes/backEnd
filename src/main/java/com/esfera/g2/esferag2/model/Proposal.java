package com.esfera.g2.esferag2.model;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProposal;

    @Column(nullable = false)
    private String service;

    @Column(nullable = false)
    private java.sql.Timestamp proposalDate;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false, length = 5000)
    private String description;

    @Column(length = 209715200)
    private byte[] file;

    @ManyToOne
    @JoinColumn(name = "Statusproposal_idStatusproposal")
    private StatusProposal idStatusProposal;

    @ManyToOne
    @JoinColumn(name = "Lead_idLead")
    private Lead idLead;

    public Long getIdProposal() {
        return idProposal;
    }

    public void setIdProposal(Long idProposal) {
        this.idProposal = idProposal;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Timestamp getProposalDate() {
        return proposalDate;
    }

    public void setProposalDate(Timestamp proposalDate) {
        this.proposalDate = proposalDate;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public Lead getIdLead() {
        return idLead;
    }

    public void setIdLead(Lead idLead) {
        this.idLead = idLead;
    }

    public StatusProposal getIdStatusProposal() {
        return idStatusProposal;
    }

    public void setIdStatusProposal(StatusProposal idStatusProposal) {
        this.idStatusProposal = idStatusProposal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proposal proposal = (Proposal) o;
        return Objects.equals(idProposal, proposal.idProposal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProposal);
    }
}
