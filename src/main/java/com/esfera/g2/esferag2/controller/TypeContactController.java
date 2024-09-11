package com.esfera.g2.esferag2.controller;

import com.esfera.g2.esferag2.model.TypeContact;
import com.esfera.g2.esferag2.repository.TypeContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/typeContact")
public class TypeContactController {

    @Autowired
    private TypeContactRepository typeContactRepository;

    @GetMapping
    public List<TypeContact> getAllTypeContact() {
        return typeContactRepository.findAll();
    }

    @GetMapping("/{id}")
    public TypeContact getTypeContactById(@PathVariable Long id){
        return typeContactRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public TypeContact createTypeContact(@RequestBody TypeContact typeContact){
        return typeContactRepository.save(typeContact);
    }

    @PutMapping("/{id}")
    public TypeContact updateTypeContact(@PathVariable Long id, @RequestBody TypeContact typeContactDetails){
        return typeContactRepository.findById(id)
                .map(typeContact -> {
                    typeContact.setType(typeContactDetails.getType());
                    return typeContactRepository.save(typeContact);
                })
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTypeContact(@PathVariable Long id) {
        try {
            typeContactRepository.deleteById(id);
            return new ResponseEntity<>("Deletado com sucesso", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("ID não encontrado!", HttpStatus.NOT_FOUND);
        }
    }
}
