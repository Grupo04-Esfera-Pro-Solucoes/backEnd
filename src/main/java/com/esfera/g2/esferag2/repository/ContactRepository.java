package com.esfera.g2.esferag2.repository;

import com.esfera.g2.esferag2.model.Client;
import com.esfera.g2.esferag2.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByClient(Client idClient);
    List<Contact> findAllByClientIn(List<Client> clients);
    List<Contact> findAllByClient(Client client);
    List<Contact> findContactsByClientIdClient(Long id);
}
