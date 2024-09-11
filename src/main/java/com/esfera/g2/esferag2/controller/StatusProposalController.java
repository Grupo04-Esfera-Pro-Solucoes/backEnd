package com.esfera.g2.esferag2.controller;

import com.esfera.g2.esferag2.model.StatusProposal;
import com.esfera.g2.esferag2.repository.StatusProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statusProposal")
public class StatusProposalController {

    @Autowired
    private StatusProposalRepository statusProposalRepository;

    @GetMapping
    public List<StatusProposal> getAllStatusProposals(){
        return statusProposalRepository.findAll();
    }

    @GetMapping("/{id}")
    public StatusProposal getStatusProposalById(@PathVariable Long id){
        return statusProposalRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public StatusProposal createStatusProposal(@RequestBody StatusProposal statusProposal){
        return statusProposalRepository.save(statusProposal);
    }

    @PutMapping("/{id}")
    public StatusProposal updateStatusProposal(@PathVariable Long id, @RequestBody StatusProposal statusProposalDetails){
        return statusProposalRepository.findById(id)
                .map(statusProposal -> {
                    statusProposal.setName(statusProposalDetails.getName());
                    return statusProposalRepository.save(statusProposal);
                })
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStatusProposal(@PathVariable Long id){
        try{
            statusProposalRepository.deleteById(id);
            return new ResponseEntity<>("Deletado com sucesso!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("ID não encontrado!", HttpStatus.NOT_FOUND);
        }
    }

}
