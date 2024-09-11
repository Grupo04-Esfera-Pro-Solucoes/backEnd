package com.esfera.g2.esferag2.repository;

import com.esfera.g2.esferag2.model.Address;
import com.esfera.g2.esferag2.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByClient(Client idClient);
    List<Address> findAllByClientIn(Collection<Client> clients);
    List<Address> findAllByClient(Client client);
}
