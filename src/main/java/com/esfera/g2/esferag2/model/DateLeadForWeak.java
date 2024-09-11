package com.esfera.g2.esferag2.model;

import java.util.List;

public class DateLeadForWeak {
    private List<Long> leadCount;
    private List<String> dateLead;
    private double crescimentoPercentual;

    public DateLeadForWeak() {
    }

    public DateLeadForWeak(List<Long> leadCount, List<String> dateLead) {
        this.leadCount = leadCount;
        this.dateLead = dateLead;
    }

    public DateLeadForWeak(List<Long> leadCount, List<String> dateLead, double crescimentoPercentual) {
        this.leadCount = leadCount;
        this.dateLead = dateLead;
        this.crescimentoPercentual = crescimentoPercentual;
    }

    public List<Long> getLeadCount() {
        return leadCount;
    }

    public void setLeadCount(List<Long> leadCount) {
        this.leadCount = leadCount;
    }

    public List<String> getDateLead() {
        return dateLead;
    }

    public void setDateLead(List<String> dateLead) {
        this.dateLead = dateLead;
    }

    public double getCrescimentoPercentual() {
        return crescimentoPercentual;
    }

    public void setCrescimentoPercentual(double crescimentoPercentual) {
        this.crescimentoPercentual = crescimentoPercentual;
    }
}
