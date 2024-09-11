package com.esfera.g2.esferag2;

import com.esfera.g2.esferag2.controller.LeadController;
import com.esfera.g2.esferag2.model.DateLeadForWeak;
import com.esfera.g2.esferag2.model.Lead;
import com.esfera.g2.esferag2.model.LeadResult;
import com.esfera.g2.esferag2.repository.LeadRepository;
import com.esfera.g2.esferag2.repository.ProposalRepository;
import com.esfera.g2.esferag2.service.LeadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LeadControllerTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private ProposalRepository proposalRepository;

    @Mock
    private LeadService leadService;

    @InjectMocks
    private LeadController leadController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLeadStatistics() {
        Long idUser = 1L;
        List<Object[]> statistics = Arrays.asList(new Object[]{"stat1"}, new Object[]{"stat2"});
        when(leadService.getLeadStatistics(idUser, "all")).thenReturn(statistics);

        ResponseEntity<List<Object[]>> response = leadController.getLeadStatistics(idUser, "all");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(statistics, response.getBody());
    }

    @Test
    void testExportLeads() {
        Long idUser = 1L;
        List<Lead> leads = Arrays.asList(new Lead(), new Lead());
        when(leadRepository.findLeadsByIdClientUserIdUser(idUser)).thenReturn(leads);

        ResponseEntity<?> response = leadController.exportLeads(idUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(leads, response.getBody());
    }

    @Test
    void testGetAllLeads() {
        Long idUser = 1L;
        int page = 0;
        int size = 20;
        String sortBy = "idLead";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Lead> leads = new PageImpl<>(Arrays.asList(new Lead(), new Lead()));
        when(leadRepository.findAllByIdClientUserIdUser(idUser, pageable)).thenReturn(leads);

        Page<Lead> response = leadController.getAllLeads(idUser, page, size, sortBy);

        assertEquals(leads, response);
    }

    @Test
    void testGetAllLeadsToday() {
        Long idUser = 1L;
        int page = 0;
        int size = 20;
        String sortBy = "idLead";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        LocalDate currentDate = LocalDate.now();
        Page<Lead> leads = new PageImpl<>(Arrays.asList(new Lead(), new Lead()));
        when(leadRepository.findAllByIdClientUserIdUserAndDate(idUser, currentDate, pageable)).thenReturn(leads);

        Page<Lead> response = leadController.getAllLeadsToday(idUser, page, size, sortBy);

        assertEquals(leads, response);
    }

    @Test
    void testGetLeadById() {
        Long id = 1L;
        Long idUser = 1L;
        Lead lead = new Lead();
        when(leadRepository.findByIdLeadAndIdClientUserIdUser(id, idUser)).thenReturn(lead);

        Lead response = leadController.getLeadById(id, idUser);

        assertEquals(lead, response);
    }

    @Test
    void testCreateLead() {
        Lead lead = new Lead();
        when(leadRepository.save(lead)).thenReturn(lead);

        Lead response = leadController.createLead(lead);

        assertEquals(lead, response);
    }

    @Test
    void testUpdateLead() {
        Long id = 1L;
        Lead leadDetails = new Lead();
        leadDetails.setContact("new contact");
        leadDetails.setDate(Timestamp.valueOf("2023-06-15 00:00:00"));
        leadDetails.setDuration("new duration");
        leadDetails.setDescription("new description");
        leadDetails.setCallTime("new call time");
        leadDetails.setResult(new LeadResult());

        Lead existingLead = new Lead();
        when(leadRepository.findById(id)).thenReturn(Optional.of(existingLead));
        when(leadRepository.save(existingLead)).thenReturn(existingLead);

        Lead response = leadController.updateLead(id, leadDetails);

        assertEquals(leadDetails.getContact(), response.getContact());
        assertEquals(leadDetails.getDate(), response.getDate());
        assertEquals(leadDetails.getDuration(), response.getDuration());
        assertEquals(leadDetails.getDescription(), response.getDescription());
        assertEquals(leadDetails.getCallTime(), response.getCallTime());
        assertEquals(leadDetails.getResult(), response.getResult());
    }

    @Test
    void testDeleteLead() {
        Long id = 1L;

        when(leadRepository.existsById(id)).thenReturn(true);
        when(proposalRepository.existsByIdLeadIdLead(id)).thenReturn(false);

        ResponseEntity<?> response = leadController.deleteLead(id);

        verify(leadRepository, times(1)).deleteById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteLeadNotFound() {
        Long id = 1L;

        when(leadRepository.existsById(id)).thenReturn(false);

        ResponseEntity<?> response = leadController.deleteLead(id);

        verify(leadRepository, times(0)).deleteById(id);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteLeadConflict() {
        Long id = 1L;

        when(leadRepository.existsById(id)).thenReturn(true);
        when(proposalRepository.existsByIdLeadIdLead(id)).thenReturn(true);

        ResponseEntity<?> response = leadController.deleteLead(id);

        verify(leadRepository, times(0)).deleteById(id);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testGetLeadsByContact() {
        Long idUser = 1L;
        String name = "test";
        int page = 0;
        int size = 20;
        String sortBy = "idLead";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Lead> leads = new PageImpl<>(Arrays.asList(new Lead(), new Lead()));
        when(leadRepository.findLeadsByIdClientNameContainingIgnoreCaseAndIdClientUserIdUser(name, idUser, pageable)).thenReturn(leads);

        Page<Lead> response = leadController.getLeadsByContact(name, idUser, page, size, sortBy);

        assertEquals(leads, response);
    }

}