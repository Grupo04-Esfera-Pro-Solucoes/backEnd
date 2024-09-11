package com.esfera.g2.esferag2.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esfera.g2.esferag2.repository.LeadRepository;

@Service
public class LeadService {

    @Autowired
    private LeadRepository leadRepository;

    public List<Object[]> getLeadStatistics(Long idUser, String period) {
        return leadRepository.countLeadsByResult(idUser, period);
    }
}
