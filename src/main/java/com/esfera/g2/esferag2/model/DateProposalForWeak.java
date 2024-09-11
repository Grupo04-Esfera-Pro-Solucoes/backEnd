package com.esfera.g2.esferag2.model;

import java.util.List;

public class DateProposalForWeak {
    private List<Long> proposalCount;
    private List<String> dateProposal;
    private double crescimentoPercentualProposal;

    public DateProposalForWeak() {
    }

    public DateProposalForWeak(List<Long> proposalCount, List<String> dateProposal) {
        this.proposalCount = proposalCount;
        this.dateProposal = dateProposal;
    }

    public DateProposalForWeak(List<Long> proposalCount, List<String> dateProposal, double crescimentoPercentualProposal) {
        this.proposalCount = proposalCount;
        this.dateProposal = dateProposal;
        this.crescimentoPercentualProposal = crescimentoPercentualProposal;
    }

    public List<Long> getProposalCount() {
        return proposalCount;
    }

    public void setProposalCount(List<Long> proposalCount) {
        this.proposalCount = proposalCount;
    }

    public List<String> getDateProposal() {
        return dateProposal;
    }

    public void setDateProposal(List<String> dateProposal) {
        this.dateProposal = dateProposal;
    }

    public double getcrescimentoPercentualProposal() {
        return crescimentoPercentualProposal;
    }

    public void setcrescimentoPercentualProposal(double crescimentoPercentualProposal) {
        this.crescimentoPercentualProposal = crescimentoPercentualProposal;
    }
}
