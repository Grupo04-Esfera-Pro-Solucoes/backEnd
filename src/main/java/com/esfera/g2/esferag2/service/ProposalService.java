package com.esfera.g2.esferag2.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esfera.g2.esferag2.repository.ProposalRepository;

@Service
public class ProposalService {

    @Autowired
    private ProposalRepository proposalRepository;

    public List<Object[]> getProposalStatistics(Long idUser, String period) {
        return proposalRepository.countProposalsByStatus(idUser, period);
    }
}