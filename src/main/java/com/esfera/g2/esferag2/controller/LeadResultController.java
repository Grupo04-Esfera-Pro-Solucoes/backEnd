package com.esfera.g2.esferag2.controller;

import com.esfera.g2.esferag2.model.LeadResult;
import com.esfera.g2.esferag2.repository.LeadResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leadResult")
public class LeadResultController {

    @Autowired
    private LeadResultRepository leadResultRepository;

    @GetMapping
    public List<LeadResult> getAllLeadResults() {
        return leadResultRepository.findAll();
    }

    @GetMapping("/{id}")
    public LeadResult getLeadResultById(@PathVariable Long id) {
        return leadResultRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public LeadResult createLeadResult(@RequestBody LeadResult leadResult) {
        return leadResultRepository.save(leadResult);
    }

    @PutMapping("/{id}")
    public LeadResult updateLeadResult(@PathVariable Long id, @RequestBody LeadResult leadResultDetails) {
        return leadResultRepository.findById(id)
                .map(leadResult -> {
                    leadResult.setResult(leadResultDetails.getResult());
                    return leadResultRepository.save(leadResult);
                })
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteLeadResult(@PathVariable Long id) {
        leadResultRepository.deleteById(id);
    }
}
