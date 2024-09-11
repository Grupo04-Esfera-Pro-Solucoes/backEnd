package com.esfera.g2.esferag2;

import com.esfera.g2.esferag2.controller.ClientAddressContactDTOController;
import com.esfera.g2.esferag2.controller.TypeContactController;
import com.esfera.g2.esferag2.model.*;
import com.esfera.g2.esferag2.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClientAddressContactDTOControllerTest {

    @InjectMocks
    private ClientAddressContactDTOController controller;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private TypeContactController typeContactController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LeadRepository leadRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddClientAddressContact_Success() {
        // Mocking TypeContactController responses
        TypeContact cellphoneType = new TypeContact();
        cellphoneType.setIdTypeContact(1L);
        cellphoneType.setType("cellphone");

        TypeContact phoneType = new TypeContact();
        phoneType.setIdTypeContact(2L);
        phoneType.setType("phone");

        TypeContact whatsappType = new TypeContact();
        whatsappType.setIdTypeContact(3L);
        whatsappType.setType("whatsapp");

        TypeContact emailType = new TypeContact();
        emailType.setIdTypeContact(4L);
        emailType.setType("email");

        when(typeContactController.getTypeContactById(1L)).thenReturn(cellphoneType);
        when(typeContactController.getTypeContactById(2L)).thenReturn(phoneType);
        when(typeContactController.getTypeContactById(3L)).thenReturn(whatsappType);
        when(typeContactController.getTypeContactById(4L)).thenReturn(emailType);

        Client client = new Client();
        Address address = new Address("12345", "Country", "State", "City", "Street", "10");
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("123456789", cellphoneType, client));
        contacts.add(new Contact("123456789", phoneType, client));
        contacts.add(new Contact("123456789", whatsappType, client));
        contacts.add(new Contact("email@email.com", emailType, client));

        ClientAddressContactDTO dto = new ClientAddressContactDTO();
        dto.setClient(client);
        dto.setAddress(address);
        dto.setContact(contacts);

        ResponseEntity<?> response = controller.addClientAddressContact(dto);

        verify(clientRepository, times(1)).save(client);
        verify(addressRepository, times(1)).save(address);
        verify(contactRepository, times(4)).save(any(Contact.class));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Adicionado com sucesso!", response.getBody());
    }

    @Test
    void testAddClientAddressContact_Failure() {
        Client client = new Client();
        Address address = new Address("12345", "Country", "State", "City", "Street", "10");
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact());

        ClientAddressContactDTO dto = new ClientAddressContactDTO();
        dto.setClient(client);
        dto.setAddress(address);
        dto.setContact(contacts);

        doThrow(new RuntimeException()).when(clientRepository).save(any(Client.class));

        ResponseEntity<?> response = controller.addClientAddressContact(dto);

        verify(clientRepository, times(1)).save(client);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro ao adicionar!", response.getBody());
    }

    @Test
    void testListAllClientAddressContact() {
        Long idUser = 1L;
        int page = 0;
        int size = 20;
        String sortBy = "idClient";

        Client client = new Client();
        client.setIdClient(1L);
        List<Client> clients = Arrays.asList(client);
        Page<Client> clientPage = new PageImpl<>(clients);

        when(clientRepository.findAllByUserIdUser(eq(idUser), any(Pageable.class))).thenReturn(clientPage);
        when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));
        when(addressRepository.findAllByClient(any(Client.class))).thenReturn(Arrays.asList(new Address("12345", "Country", "State", "City", "Street", "10")));
        when(contactRepository.findAllByClient(any(Client.class))).thenReturn(Arrays.asList(new Contact()));

        Page<ClientAddressContactDTO> response = controller.listAllClientAddressContact(idUser, page, size, sortBy);

        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testDeleteClientAddressContact_Success() {
        Long idClientDTO = 1L;
        Long idUser = 1L;

        Client client = new Client();
        client.setIdClient(idClientDTO);
        when(clientRepository.existsById(idClientDTO)).thenReturn(true);
        when(leadRepository.existsByIdClientIdClient(idClientDTO)).thenReturn(false);
        when(clientRepository.findByIdClientAndUserIdUser(idClientDTO, idUser)).thenReturn(client);

        Address address = new Address();
        address.setIdAddress(1L);
        when(addressRepository.findByClient(client)).thenReturn(Arrays.asList(address));

        Contact contact = new Contact();
        contact.setIdContact(1L);
        when(contactRepository.findByClient(client)).thenReturn(Arrays.asList(contact));

        ResponseEntity<?> response = controller.deleteClientAddressContact(idClientDTO, idUser);

        verify(addressRepository, times(1)).deleteById(address.getIdAddress());
        verify(contactRepository, times(1)).deleteById(contact.getIdContact());
        verify(clientRepository, times(1)).deleteById(client.getIdClient());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deletado com sucesso!", response.getBody());
    }

    @Test
    void testDeleteClientAddressContact_ClientNotFound() {
        Long idClientDTO = 1L;
        Long idUser = 1L;

        when(clientRepository.existsById(idClientDTO)).thenReturn(false);

        ResponseEntity<?> response = controller.deleteClientAddressContact(idClientDTO, idUser);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cliente não encontrado!", response.getBody());
    }

    @Test
    void testDeleteClientAddressContact_Conflict() {
        Long idClientDTO = 1L;
        Long idUser = 1L;

        when(clientRepository.existsById(idClientDTO)).thenReturn(true);
        when(leadRepository.existsByIdClientIdClient(idClientDTO)).thenReturn(true);

        ResponseEntity<?> response = controller.deleteClientAddressContact(idClientDTO, idUser);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Existem leads associados a este cliente, não é possível deletar", response.getBody());
    }

    @Test
    void testUpdateClientAddressContact() {
        Long id = 1L;
        Client existingClient = new Client();
        existingClient.setIdClient(id);
        existingClient.setName("Old Name");
        existingClient.setCpfCnpj("Old CPF/CNPJ");
        existingClient.setCompany("Old Company");
        existingClient.setRole("Old Role");

        Address existingAddress = new Address("12345", "Country", "State", "City", "Street", "10");
        existingAddress.setClient(existingClient);

        ClientAddressContactDTO dto = new ClientAddressContactDTO();
        Client updatedClient = new Client();
        updatedClient.setName("New Name");
        updatedClient.setCpfCnpj("New CPF/CNPJ");
        updatedClient.setCompany("New Company");
        updatedClient.setRole("New Role");
        dto.setClient(updatedClient);

        Address updatedAddress = new Address("54321", "NewCountry", "NewState", "NewCity", "NewStreet", "20");
        dto.setAddress(updatedAddress);

        List<Contact> updatedContacts = new ArrayList<>();
        TypeContact typeContact = new TypeContact();
        typeContact.setIdTypeContact(1L);
        updatedContacts.add(new Contact("new_data", typeContact, existingClient));
        dto.setContact(updatedContacts);

        when(clientRepository.findById(id)).thenReturn(Optional.of(existingClient));
        when(addressRepository.findByClient(existingClient)).thenReturn(Arrays.asList(existingAddress));
        when(contactRepository.findByClient(existingClient)).thenReturn(updatedContacts);

        ClientAddressContactDTO response = controller.updateClientAddressContact(id, dto);

        verify(clientRepository, times(1)).save(existingClient);
        verify(addressRepository, times(1)).save(existingAddress);
        verify(contactRepository, times(1)).save(any(Contact.class));

        assertEquals("New Name", response.getClient().getName());
        assertEquals("54321", response.getAddress().getZipCode());
        assertEquals("new_data", response.getContact().get(0).getData());
    }
}