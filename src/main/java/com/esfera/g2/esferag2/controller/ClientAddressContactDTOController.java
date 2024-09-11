package com.esfera.g2.esferag2.controller;

import com.esfera.g2.esferag2.model.*;
import com.esfera.g2.esferag2.repository.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/client-address-contact")
public class ClientAddressContactDTOController {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private TypeContactController typeContactController;
    @Autowired
    private UserRepository userRepository;


    //Validation's
    @Autowired
    private LeadRepository leadRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addClientAddressContact(@RequestBody ClientAddressContactDTO clientAddressContactDTO) {
        try {
            clientRepository.save(clientAddressContactDTO.getClient());
            Address address = clientAddressContactDTO.getAddress();
            address.setClient(clientAddressContactDTO.getClient());
            addressRepository.save(address);
            for (Contact contact : clientAddressContactDTO.getContact()) {
                contact.setClient(clientAddressContactDTO.getClient());
                contactRepository.save(contact);
            }
            return ResponseEntity.status(201).body("Adicionado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao adicionar!");
        }
    }

    @GetMapping("/all/{idUser}")
    public Page<ClientAddressContactDTO> listAllClientAddressContact(
            @PathVariable Long idUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "idClient") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Client> clientsPage = clientRepository.findAllByUserIdUser(idUser, pageable);

        // Obtém os IDs dos clientes apenas na página atual
        List<Long> clientIds = clientsPage.getContent().stream()
                .map(Client::getIdClient)
                .collect(Collectors.toList());

        // Obtém os DTOs de clientes com endereços e contatos
        List<ClientAddressContactDTO> dtos = findClientDetailsWithContacts(clientIds);

        // Constrói e retorna a página de DTOs
        return new PageImpl<>(dtos, pageable, clientsPage.getTotalElements());
    }

    public List<ClientAddressContactDTO> findClientDetailsWithContacts(List<Long> clientIds) {
        List<ClientAddressContactDTO> dtos = new ArrayList<>();
        for (Long clientId : clientIds) {
            Client client = clientRepository.findById(clientId).orElse(null);
            if (client != null) {
                List<Address> addresses = addressRepository.findAllByClient(client);
                List<Contact> contacts = contactRepository.findAllByClient(client);

                for (Address address : addresses) {
                    dtos.add(new ClientAddressContactDTO(client, address, contacts));
                }
            }
        }
        return dtos;
    }


    @PostMapping("/import/{idUser}")
    public ResponseEntity<?> addClientAddressContact(@RequestParam("file") MultipartFile file, @PathVariable Long idUser) {
        try {
            User user = userRepository.findById(idUser).orElseThrow();
            Reader reader = new InputStreamReader(file.getInputStream());
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());

            for (CSVRecord record : csvParser) {
                String name = record.get("Nome");
                String cpf = record.get("cpf");
                String company = record.get("empresa");
                String role = record.get("cargo");

                //TODO - Implementar isso isso
                String date = record.get("data");


                String email = record.get("email");
                String phone = record.get("telefone");
                String cellphone = record.get("celular");
                String whatsapp = record.get("whatsapp");
                String zipCode = record.get("cep");
                String country = record.get("pais");
                String state = record.get("estado");
                String city = record.get("cidade");
                String street = record.get("rua");
                String number = record.get("numero");

                Client client = new Client(name, cpf, company, role, new Timestamp(System.currentTimeMillis()), user);
                clientRepository.save(client);

                Address address = new Address(zipCode, country, state, city, street, number);
                address.setClient(client);
                addressRepository.save(address);

                List<Contact> contacts = new ArrayList<>();
                contacts.add(new Contact(cellphone, typeContactController.getTypeContactById(1L), client));
                contacts.add(new Contact(phone, typeContactController.getTypeContactById(2L), client));
                contacts.add(new Contact(whatsapp, typeContactController.getTypeContactById(3L), client));
                contacts.add(new Contact(email, typeContactController.getTypeContactById(4L), client));

                contactRepository.saveAll(contacts);
            }
            csvParser.close();
            return ResponseEntity.status(201).body("Arquivo CSV processado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar o arquivo CSV!");
        }
    }

    @DeleteMapping("/delete/{idClientDTO}/{idUser}")
    public ResponseEntity<?> deleteClientAddressContact(@PathVariable Long idClientDTO,
                                                        @PathVariable Long idUser) {
        if (!clientRepository.existsById(idClientDTO)) {
            return ResponseEntity.badRequest().body("Cliente não encontrado!");
        } else if (leadRepository.existsByIdClientIdClient(idClientDTO)) {
            return new ResponseEntity<>("Existem leads associados a este cliente, não é possível deletar", HttpStatus.CONFLICT);
        }
        try {
            Client c = clientRepository.findByIdClientAndUserIdUser(idClientDTO, idUser);

            for (Address address : addressRepository.findByClient(c)) {
                addressRepository.deleteById(address.getIdAddress());
            }
            for (Contact contact : contactRepository.findByClient(c)) {
                contactRepository.deleteById(contact.getIdContact());
            }

            clientRepository.deleteById(c.getIdClient());
            return ResponseEntity.status(200).body("Deletado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao deletar!");
        }
    }

    @DeleteMapping("/delete/{idUser}")
    public ResponseEntity<?> deleteClientAddressContacts(@RequestBody List<Long> ids, @PathVariable Long idUser) {
        List<Long> deleteds = new ArrayList<>();
        List<Long> notDeletedDueToLeads = new ArrayList<>();

        for (Long id : ids) {
            ResponseEntity<?> response = deleteClientAddressContact(id, idUser);
            if (response.getStatusCode() == HttpStatus.CONFLICT) {
                notDeletedDueToLeads.add(id);
            } else if (!response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.badRequest().body("Erro ao deletar, cliente cujo ID: " + id + " possui proposal cadastrada!");
            } else {
                deleteds.add(id);
            }
        }

        if (!notDeletedDueToLeads.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Clientes com os seguintes IDs não puderam ser deletados devido a leads associados: " + notDeletedDueToLeads);
        }
        return ResponseEntity.ok("Deletado com sucesso. Total deletados: " + deleteds.size());
    }


    @PutMapping("/update/{id}")
    public ClientAddressContactDTO updateClientAddressContact(@PathVariable Long id, @RequestBody ClientAddressContactDTO clientAddressContactDTO) {
        Client client = clientRepository.findById(id).orElseThrow();
        client.setName(clientAddressContactDTO.getClient().getName());
        client.setCpfCnpj(clientAddressContactDTO.getClient().getCpfCnpj());
        client.setCompany(clientAddressContactDTO.getClient().getCompany());
        client.setRole(clientAddressContactDTO.getClient().getRole());
        client.setDate(clientAddressContactDTO.getClient().getDate());
        clientRepository.save(client);

        Address address = addressRepository.findByClient(client).get(0);
        address.setZipCode(clientAddressContactDTO.getAddress().getZipCode());
        address.setCountry(clientAddressContactDTO.getAddress().getCountry());
        address.setState(clientAddressContactDTO.getAddress().getState());
        address.setCity(clientAddressContactDTO.getAddress().getCity());
        address.setStreet(clientAddressContactDTO.getAddress().getStreet());
        address.setNumber(clientAddressContactDTO.getAddress().getNumber());
        addressRepository.save(address);

        List<Contact> contacts = contactRepository.findByClient(client);

        for (Contact contact : contacts) {
            if (contact.getIdTypeContact().getIdTypeContact() == 1) {
                contact.setData(clientAddressContactDTO.getContact().get(0).getData());
            } else if (contact.getIdTypeContact().getIdTypeContact() == 2) {
                contact.setData(clientAddressContactDTO.getContact().get(1).getData());
            } else if (contact.getIdTypeContact().getIdTypeContact() == 3) {
                contact.setData(clientAddressContactDTO.getContact().get(2).getData());
            } else if (contact.getIdTypeContact().getIdTypeContact() == 4) {
                contact.setData(clientAddressContactDTO.getContact().get(3).getData());
            }
            contactRepository.save(contact);
        }
        return clientAddressContactDTO;
    }

    @GetMapping("/{id}/{idUser}")
    public ClientAddressContactDTO getClientAddressContactById(@PathVariable Long id, @PathVariable Long idUser) {
            Client client = clientRepository.findByIdClientAndUserIdUser(id, idUser);
            Address address = addressRepository.findByClient(client).get(0);
            List<Contact> contacts = contactRepository.findByClient(client);
            ClientAddressContactDTO clientAddressContactDTO = new ClientAddressContactDTO();
            clientAddressContactDTO.setClient(client);
            clientAddressContactDTO.setAddress(address);
            clientAddressContactDTO.setContact(contacts);
            return clientAddressContactDTO;
    }

    @GetMapping("/name/{name}/{idUser}")
    public Page<ClientAddressContactDTO> getClientAddressContactByName(@PathVariable String name,
                                                                       @PathVariable Long idUser,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "20") int size,
                                                                       @RequestParam(defaultValue = "idClient") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Client> clientsPage = clientRepository.findClientsByNameContainingIgnoreCaseAndUserIdUser(name, idUser, pageable);

        // Obtém os IDs dos clientes apenas na página atual
        List<Long> clientIds = clientsPage.getContent().stream()
                .map(Client::getIdClient)
                .collect(Collectors.toList());

        // Obtém os DTOs de clientes com endereços e contatos
        List<ClientAddressContactDTO> dtos = findClientDetailsWithContacts(clientIds);

        // Constrói e retorna a página de DTOs
        return new PageImpl<>(dtos, pageable, clientsPage.getTotalElements());
    }
}
