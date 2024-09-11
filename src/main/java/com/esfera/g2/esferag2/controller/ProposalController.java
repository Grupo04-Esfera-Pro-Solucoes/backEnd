package com.esfera.g2.esferag2.controller;

import com.esfera.g2.esferag2.model.DateProposalForWeak;
import com.esfera.g2.esferag2.model.Lead;
import com.esfera.g2.esferag2.model.Proposal;
import com.esfera.g2.esferag2.model.StatusProposal;
import com.esfera.g2.esferag2.repository.LeadRepository;
import com.esfera.g2.esferag2.repository.ProposalRepository;
import com.esfera.g2.esferag2.repository.StatusProposalRepository;
import com.esfera.g2.esferag2.service.ProposalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/proposal")
public class ProposalController {

    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private LeadRepository leadRepository;
    @Autowired
    private StatusProposalRepository statusProposalRepository;
    @Autowired
    private ProposalService proposalService;

    private static final Logger logger = Logger.getLogger(ProposalController.class.getName());

    @GetMapping("/faturamento/{idUser}")
    public ResponseEntity<Map<String, Object>> getFaturamento(@PathVariable Long idUser) {
        try {
            Double totalFaturamento = proposalRepository.sumValue(idUser);
            Double faturamentoMesAnterior = proposalRepository.sumValueLastMonth(idUser);

            if (totalFaturamento == null) totalFaturamento = 0.0;
            if (faturamentoMesAnterior == null) faturamentoMesAnterior = 0.0;

            double crescimentoPercentual = 0;
            if (faturamentoMesAnterior > 0) {
                crescimentoPercentual = ((totalFaturamento - faturamentoMesAnterior) / faturamentoMesAnterior) * 100;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("totalFaturamento", totalFaturamento);
            response.put("crescimentoPercentual", crescimentoPercentual);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Erro ao calcular o faturamento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/statistics/{idUser}")
    public ResponseEntity<List<Object[]>> getProposalStatistics(@PathVariable Long idUser, @RequestParam(required = false, defaultValue = "all") String period) {
        List<Object[]> statistics = proposalService.getProposalStatistics(idUser, period);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/export/{idUser}")
    public ResponseEntity<?> exportProposals(@PathVariable Long idUser) {
        return ResponseEntity.ok(proposalRepository.findByIdLeadIdClientUserIdUser(idUser));
    }
    //

    @GetMapping("/all/{idUser}")
    public Page<Proposal> getAllProposals(
            @PathVariable Long idUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "idProposal") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return proposalRepository.findByIdLeadIdClientUserIdUser(idUser, pageable);
    }

    @GetMapping("/{id}/{idUser}")
    public Proposal getProposalById(@PathVariable Long id,
                                    @PathVariable Long idUser) {
        return proposalRepository.findByIdProposalAndIdLeadIdClientUserIdUser(id, idUser);
    }

    @GetMapping("/graph/proposalsmonth/{idUser}")
    public DateProposalForWeak getProposalsForMonthByUser(@PathVariable Long idUser) {
        ArrayList<Long> proposals = new ArrayList<>();
        ArrayList<String> proposalsDates = new ArrayList<>();
        Double totalFaturamento = proposalRepository.sumValue2(idUser);   
        Double faturamentoMesAnterior = proposalRepository.sumValueLastMonth2(idUser);
        double crescimentoPercentualPropostas = 0;

        if (totalFaturamento == null) totalFaturamento = 0.0;
        if (faturamentoMesAnterior == null) faturamentoMesAnterior = 0.0;

        if (faturamentoMesAnterior > 0) {
            crescimentoPercentualPropostas = ((totalFaturamento - faturamentoMesAnterior) / faturamentoMesAnterior) * 100;
        }

        Calendar startOfTheDay = getFirstTimeOfTheDay(-30);
        Calendar endOfTheDay = getLastTimeOfTheDay(-30);

        startOfTheDay.add(Calendar.DAY_OF_MONTH, 1);
        endOfTheDay.add(Calendar.DAY_OF_MONTH, 1);

        for (int i = 0; i < 30; i++) {
            proposals.add(proposalRepository.countByProposalDateBetweenAndIdLeadIdClientUserIdUser(
                new Timestamp(startOfTheDay.getTimeInMillis()),
                new Timestamp(endOfTheDay.getTimeInMillis()),
                idUser
            ));

            proposalsDates.add(formatarData(startOfTheDay.getTime()));

            startOfTheDay.add(Calendar.DAY_OF_MONTH, 1);
            endOfTheDay.add(Calendar.DAY_OF_MONTH, 1);
        }

        return new DateProposalForWeak(proposals, proposalsDates, crescimentoPercentualPropostas);
    }

    private String formatarData(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(data);
    }

    private Calendar getFirstTimeOfTheDay(int dayLess){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.add(Calendar.DAY_OF_MONTH, dayLess);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 000);

        return calendar;
    }

    private Calendar getLastTimeOfTheDay(int dayLess){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.add(Calendar.DAY_OF_MONTH, dayLess);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar;
    }

    @PostMapping(consumes = "multipart/form-data")
    public Proposal createProposal(@RequestParam("idLead") Long idLead,
                                   @RequestParam("completionDate") String completionDateStr,
                                   @RequestParam("service") String service,
                                   @RequestParam("value") String value,
                                   @RequestParam("description") String description,
                                   @RequestParam("idStatusProposal") Long idStatusProposal,
                                   @RequestPart(value = "file", required = false) MultipartFile file) {
        if (idLead == null || completionDateStr == null) {
            throw new IllegalArgumentException("O campo idClient e a data de conclusão são obrigatórios.");
        }
        Lead lead = leadRepository.findById(idLead).orElseThrow();
        StatusProposal statusProposal = statusProposalRepository.findById(idStatusProposal).orElseThrow();

        // Converter a string para Timestamp
        Timestamp completionDate = Timestamp.valueOf(completionDateStr + " 00:00:00");
        double valueTratado = (!value.isEmpty() ? Double.parseDouble(value) : 0.0);

        Proposal proposal = new Proposal();
        proposal.setIdLead(lead);
        proposal.setProposalDate(completionDate);
        proposal.setService(service);
        proposal.setValue(valueTratado);
        proposal.setDescription(description);
        proposal.setIdStatusProposal(statusProposal);

        if (file != null) {
            try {
                proposal.setFile(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace(); // TODO - Melhorar tratamento de erro
            }
        }

        return proposalRepository.save(proposal);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public Proposal updateProposal(@PathVariable Long id,
                                   @RequestParam("idLead") Long idLead,
                                   @RequestParam("idUser") Long idUser,
                                   @RequestParam("completionDate") String completionDateStr,
                                   @RequestParam("service") String service,
                                   @RequestParam("value") Double value,
                                   @RequestParam("description") String description,
                                   @RequestParam("idStatusProposal") Long idStatusProposal,
                                   @RequestPart(value = "file", required = false) MultipartFile file) {
        return proposalRepository.findById(id)
                .map(proposal -> {
                    Lead lead = leadRepository.findById(idLead).orElseThrow();
                    StatusProposal statusProposal = statusProposalRepository.findById(idStatusProposal).orElseThrow();

                    // Converter a string para Timestamp
                    Timestamp completionDate = Timestamp.valueOf(completionDateStr + " 00:00:00");


                    proposal.setIdLead(lead);
                    proposal.setProposalDate(completionDate);
                    proposal.setService(service);
                    proposal.setValue(value);
                    proposal.setDescription(description);
                    proposal.setIdStatusProposal(statusProposal);

                    if (file != null) {
                        try {
                            proposal.setFile(file.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace(); // TODO - Melhorar tratamento de erro
                        }
                    }

                    return proposalRepository.save(proposal);
                })
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProposal(@PathVariable Long id) {
        try {
            proposalRepository.deleteById(id);
            return new ResponseEntity<>("Deletado com sucesso!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("ID não encontrado!", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search/{name}/{idUser}")
    public Page<Proposal> getAllProposalsByClientName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "idProposal") String sortBy,
            @PathVariable String name,
            @PathVariable Long idUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return proposalRepository.findByIdLeadIdClientNameContainingIgnoreCaseAndIdLeadIdClientUserIdUser(name, idUser, pageable);
    }

    @GetMapping("/download/{id}/{idUser}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id,
                                               @PathVariable Long idUser) {
        Proposal proposal = proposalRepository.findByIdProposalAndIdLeadIdClientUserIdUser(id, idUser);
        byte[] file = proposal.getFile();

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF); // Defina o tipo de arquivo apropriado
        headers.setContentDispositionFormData("attachment", "proposal_" + id + ".pdf");
        headers.setContentLength(file.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(file);
    }
}
