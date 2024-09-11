package com.esfera.g2.esferag2.controller;

import com.esfera.g2.esferag2.model.Contact;
import com.esfera.g2.esferag2.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @GetMapping("/{id}")
    public Contact getContactById(@PathVariable Long id) {
        return contactRepository.findById(id).orElseThrow();
    }

    @GetMapping("/clientId/{clientId}")
    public Contact getContactsByClientId(@PathVariable Long clientId) {
        List<Contact> contacts = contactRepository.findContactsByClientIdClient(clientId);
            for (Contact contact : contacts) {
                if (contact.getIdTypeContact().getType().equals("whatsapp")) {
                    return contact;
                }
            }
        throw new RuntimeException("No whatsapp contact found for client with id " + clientId);
    }

    @PostMapping
    public Contact createContact(@RequestBody Contact contact) {
        return contactRepository.save(contact);
    }

    @PutMapping("/{id}")
    public Contact updateContact(@PathVariable Long id, @RequestBody Contact contactDetails) {
        return contactRepository.findById(id)
                .map(contact -> {
                    contact.setData(contactDetails.getData());
                    return contactRepository.save(contact);
                })
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteContact(@PathVariable Long id) {
        contactRepository.deleteById(id);
    }
}
