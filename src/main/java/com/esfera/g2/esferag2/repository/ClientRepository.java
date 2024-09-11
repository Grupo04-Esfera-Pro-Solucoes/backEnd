package com.esfera.g2.esferag2.repository;

import com.esfera.g2.esferag2.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByNameContainingIgnoreCase(String name);

    Page<Client> findAllByUserIdUser(Long idUser, Pageable pageable);
    List<Client> findClientsByCpfCnpjAndUserIdUser(String cpfCnpj, Long idUser);
    Page<Client> findClientsByNameContainingIgnoreCaseAndUserIdUser(String name, Long idUser, Pageable pageable);

    Client findByIdClientAndUserIdUser(Long idClient, Long idUser);

    List<Client> findClientsByUserIdUser(Long idUser);

}
