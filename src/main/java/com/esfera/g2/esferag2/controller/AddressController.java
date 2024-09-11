package com.esfera.g2.esferag2.controller;

import com.esfera.g2.esferag2.model.Address;
import com.esfera.g2.esferag2.model.Client;
import com.esfera.g2.esferag2.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    private final AddressRepository addressRepository;

    @Autowired
    public AddressController(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }


    @GetMapping
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    @GetMapping("/{id}")
    public Address getAddressById(@PathVariable Long id) {
        return addressRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public Address createAddress(@RequestBody Address address) {
        return addressRepository.save(address);
    }

    @PutMapping("/{id}")
    public Address updateAddress(@PathVariable Long id, @RequestBody Address addressDetails) {
        return addressRepository.findById(id)
                .map(address -> {
                    address.setStreet(addressDetails.getStreet());
                    address.setCity(addressDetails.getCity());
                    address.setState(addressDetails.getState());
                    address.setZipCode(addressDetails.getZipCode());
                    address.setNumber(addressDetails.getNumber());
                    address.setCountry(addressDetails.getCountry());
                    return addressRepository.save(address);
                })
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
        try {
            addressRepository.deleteById(id);
            return new ResponseEntity<>("Deletado com sucesso!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("ID não encontrado!", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/client/{id}")
    public List<Address> getAddressByClient(@RequestBody Client client) {
        return addressRepository.findByClient(client);
    }
}
