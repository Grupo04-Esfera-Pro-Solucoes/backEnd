package com.esfera.g2.esferag2;

import com.esfera.g2.esferag2.controller.*;
import com.esfera.g2.esferag2.model.*;
import com.esfera.g2.esferag2.repository.LeadRepository;
import com.esfera.g2.esferag2.repository.ProposalRepository;
import com.esfera.g2.esferag2.repository.StatusProposalRepository;
import com.esfera.g2.esferag2.repository.UserRepository;
import com.esfera.g2.esferag2.service.LeadService;
import com.esfera.g2.esferag2.service.ProposalService;
import com.esfera.g2.esferag2.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProposalControllerTest {

    @Mock
    private ProposalService proposalService;

    @Mock
    private ProposalRepository proposalRepository;

    @InjectMocks
    private ProposalController proposalController;

    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserController userController;

    @InjectMocks
    private ClientAddressContactDTOController clientAddressContactDTOController;

    @Mock
    private LeadService leadService;
    @Mock
    LeadRepository leadRepository;
    @InjectMocks
    private LeadController leadController;

    @Mock
    private StatusProposalRepository statusProposalRepository;
    @InjectMocks
    StatusProposalController statusProposalController = new StatusProposalController();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFaturamento() {
    }

    @Test
    void testGetProposalStatistics() {
        Long idUser = 1L;
        List<Object[]> statistics = Arrays.asList(new Object[]{"stat1"}, new Object[]{"stat2"});
        when(proposalService.getProposalStatistics(idUser, "all")).thenReturn(statistics);

        ResponseEntity<List<Object[]>> response = proposalController.getProposalStatistics(idUser, "all");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(statistics, response.getBody());
    }


    @Test
    void testGetAllProposals() {
        Long idUser = 1L;
        int page = 0;
        int size = 20;
        String sortBy = "idProposal";
        Page<Proposal> proposals = new PageImpl<>(Arrays.asList(new Proposal(), new Proposal()));
        when(proposalController.getAllProposals(idUser, page, size, sortBy)).thenReturn(proposals);

        Page<Proposal> response = proposalController.getAllProposals(idUser, page, size, sortBy);

        assertEquals(proposals, response);
    }

    @Test
    void testGetProposalById() {
        Long id = 1L;
        Long idUser = 1L;
        Proposal proposal = new Proposal();
        when(proposalController.getProposalById(id, idUser)).thenReturn(proposal);

        Proposal response = proposalController.getProposalById(id, idUser);

        assertEquals(proposal, response);
    }


    @Test
    void testGetAllProposalsByClientName() {
        Long idUser = 1L;
        String name = "test";
        int page = 0;
        int size = 20;
        String sortBy = "idProposal";
        Page<Proposal> proposals = new PageImpl<>(Arrays.asList(new Proposal(), new Proposal()));
        when(proposalController.getAllProposalsByClientName(page, size, sortBy, name, idUser)).thenReturn(proposals);

        Page<Proposal> response = proposalController.getAllProposalsByClientName(page, size, sortBy, name, idUser);

        assertEquals(proposals, response);
    }


    @Test
    void testCreateProposal() {
        Long idLead = 1L;
        Long idStatusProposal = 1L;

        Lead mockLead = new Lead();
        mockLead.setIdLead(idLead);

        StatusProposal mockStatusProposal = new StatusProposal();
        mockStatusProposal.setIdStatusProposal(idStatusProposal);

        when(leadRepository.findById(idLead)).thenReturn(Optional.of(mockLead));
        when(statusProposalRepository.findById(idStatusProposal)).thenReturn(Optional.of(mockStatusProposal));
        when(proposalRepository.save(any(Proposal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Criação da proposta
        Proposal createProposal = proposalController.createProposal(
                idLead,
                "2024-05-11",
                "new service",
                "2050",
                "new description",
                idStatusProposal,
                null // file
        );

        assertNotNull(createProposal); // Verifica se a proposta criada não é nula
        assertEquals(idLead, createProposal.getIdLead().getIdLead());
        assertEquals(Timestamp.valueOf("2024-05-11 00:00:00"), createProposal.getProposalDate());
        assertEquals("new service", createProposal.getService());
        assertEquals(2050.0, createProposal.getValue());
        assertEquals("new description", createProposal.getDescription());
        assertEquals(idStatusProposal, createProposal.getIdStatusProposal().getIdStatusProposal());
    }

    @Test
    void testDeleteProposal() {
        Long idProposal = 1L;

        Proposal existingProposal = new Proposal();
        existingProposal.setIdProposal(idProposal);

        when(proposalRepository.findById(idProposal)).thenReturn(Optional.of(existingProposal));

        ResponseEntity<?> response = proposalController.deleteProposal(idProposal);

        assertEquals(new ResponseEntity<>("Deletado com sucesso!", HttpStatus.OK), response);
        when(proposalRepository.findById(idProposal)).thenReturn(Optional.empty()); // Certifica-se de que a proposta foi deletada
        assertEquals(Optional.empty(), proposalRepository.findById(idProposal));
    }

    @Test
    void testUpdateProposal() {
        Long idProposal = 1L;
        Long idLead = 1L;
        Long idStatusProposal = 1L;

        Lead mockLead = new Lead();
        mockLead.setIdLead(idLead);

        StatusProposal mockStatusProposal = new StatusProposal();
        mockStatusProposal.setIdStatusProposal(idStatusProposal);

        Proposal existingProposal = new Proposal();
        existingProposal.setIdProposal(idProposal);
        existingProposal.setIdLead(mockLead);
        existingProposal.setIdStatusProposal(mockStatusProposal);
        existingProposal.setService("old service");
        existingProposal.setValue(1000.0);
        existingProposal.setDescription("old description");
        existingProposal.setProposalDate(Timestamp.valueOf("2024-05-10 00:00:00"));

        when(proposalRepository.findById(idProposal)).thenReturn(Optional.of(existingProposal));
        when(leadRepository.findById(idLead)).thenReturn(Optional.of(mockLead));
        when(statusProposalRepository.findById(idStatusProposal)).thenReturn(Optional.of(mockStatusProposal));
        when(proposalRepository.save(any(Proposal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Proposal createProposal = proposalController.createProposal(
                idLead,
                "2024-05-11",
                "new service",
                "2050",
                "new description",
                idStatusProposal,
                null // file
        );

        assertNotNull(createProposal); // Verifica se a proposta criada não é nula
        assertEquals(2050.0, createProposal.getValue());
        assertEquals(1000.0, proposalRepository.findById(idProposal).get().getValue());

        Proposal response = proposalController.updateProposal(
                idProposal,
                idLead,
                1L, // idUser
                "2024-05-12", // completionDateStr
                "updated service", // service
                2000.0, // value
                "updated description", // description
                idStatusProposal,
                null // file
        );

        assertNotNull(response); // Verifica se a proposta atualizada não é nula
        assertEquals("updated service", response.getService());
        assertEquals(2000.0, response.getValue());
        assertEquals(2000.0, proposalRepository.findById(idProposal).get().getValue());
        assertEquals("updated description", response.getDescription());
        assertEquals(Timestamp.valueOf("2024-05-12 00:00:00"), response.getProposalDate());
    }

}