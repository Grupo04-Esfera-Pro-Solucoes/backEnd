package com.esfera.g2.esferag2.config;

import com.esfera.g2.esferag2.model.LeadResult;
import com.esfera.g2.esferag2.model.StatusProposal;
import com.esfera.g2.esferag2.model.TypeContact;
import com.esfera.g2.esferag2.model.User;
import com.esfera.g2.esferag2.repository.LeadResultRepository;
import com.esfera.g2.esferag2.repository.StatusProposalRepository;
import com.esfera.g2.esferag2.repository.TypeContactRepository;
import com.esfera.g2.esferag2.repository.UserRepository;
import com.esfera.g2.esferag2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StatusProposalRepository statusProposalRepository;
    private final LeadResultRepository leadResultRepository;
    private final TypeContactRepository typeContactRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public DataInitializer(StatusProposalRepository statusProposalRepository, LeadResultRepository leadResultRepository, TypeContactRepository typeContactRepository, UserRepository userRepository, UserService userService) {
        this.statusProposalRepository = statusProposalRepository;
        this.leadResultRepository = leadResultRepository;
        this.typeContactRepository = typeContactRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }



    // Inicializar o banco de dados com dados padrão para cada tabela relevante
    @Override
    public void run(String... args) {
        if (typeContactRepository.count() == 0) {
            // Dados para TypeContact
            typeContactRepository.save(new TypeContact("celular"));
            typeContactRepository.save(new TypeContact("telefone"));
            typeContactRepository.save(new TypeContact("whatsapp"));
            typeContactRepository.save(new TypeContact("email"));

            System.out.println("TypeContact dummy scripts OK!");
        }
        if (leadResultRepository.count() == 0) {
            // Dados para LeadResult
            leadResultRepository.save(new LeadResult("Atendido"));
            leadResultRepository.save(new LeadResult("Desligado"));
            leadResultRepository.save(new LeadResult("Cx. Postal"));
            leadResultRepository.save(new LeadResult("Ocupado"));

            System.out.println("LeadResult dummy scripts OK!");
        }
        if (statusProposalRepository.count() == 0) {
            // Dados para StatusProposal
            statusProposalRepository.save(new StatusProposal("Fechado"));
            statusProposalRepository.save(new StatusProposal("Parado"));
            statusProposalRepository.save(new StatusProposal("Acompanhar"));
            statusProposalRepository.save(new StatusProposal("Negociação"));

            System.out.println("StatusProposal dummy scripts OK!");
        }

        if (userRepository.count() == 0) {
            // Dados para User
            String senha = userService.hashPassword("admin");
            userRepository.save(new User("Admin", "admin@admin.com", senha, "44999999999", "Administrador"));

            System.out.println("User dummy scripts OK!");
        }
    }
}
