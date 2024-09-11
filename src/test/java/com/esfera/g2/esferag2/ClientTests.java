package com.esfera.g2.esferag2;

import static org.junit.Assert.*;

import com.esfera.g2.esferag2.controller.ClientController;
import com.esfera.g2.esferag2.model.Client;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDate;

@SpringBootTest
public class ClientTests {
    ClientController clientController = new ClientController();
    @Test
    public void addClient() {
        Client client = new Client("John Doe", "11122233344", "Biopark", "Televendedor", Timestamp.valueOf(LocalDate.of(2021, 1, 1).atStartOfDay()), null);
        assertEquals("John Doe", client.getName());
        assertEquals("11122233344", client.getCpfCnpj());
        assertEquals("Biopark", client.getCompany());
        assertEquals("Televendedor", client.getRole());
        assertEquals("01/01/2021", client.getFormattedDate());
    }
}
