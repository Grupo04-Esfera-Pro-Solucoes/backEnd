package com.esfera.g2.esferag2.model;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClient;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "cpf-cnpj")
    private String cpfCnpj;

    private String company;

    private String role;

    private Timestamp date;

    @ManyToOne
    @JoinColumn(name = "user_idUser")
    private User user;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(idClient, client.idClient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idClient);
    }

    public Client() {
    }

    public Client(String name, String cpfCnpj, String company, String role, Timestamp date, User user) {
        this.name = name;
        this.cpfCnpj = cpfCnpj;
        this.company = company;
        this.role = role;
        this.date = date;
        this.user = user;
    }

    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(this.date);
    }
}
