package com.esfera.g2.esferag2.controller;

import com.esfera.g2.esferag2.model.User;
import com.esfera.g2.esferag2.repository.UserRepository;
import com.esfera.g2.esferag2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id){
        return userRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public User createUser(@RequestBody User user){
        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userDetails.getName());
                    user.setEmail(userDetails.getEmail());
                    user.setPhone(userDetails.getPhone());
                    user.setRole(userDetails.getRole());

                    if (userDetails.getPasswordHash() != null && !userDetails.getPasswordHash().isEmpty()) {
                        String hashedPassword = userService.hashPassword(userDetails.getPasswordHash());
                        user.setPasswordHash(hashedPassword);
                    }

                    userRepository.save(user);
                    return ResponseEntity.ok("Usuário atualizado com sucesso.");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/checkPassword")
    public ResponseEntity<Boolean> checkPassword(@PathVariable Long id, @RequestParam String currentPassword) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            boolean passwordMatches = userService.checkPassword(currentPassword, user.getPasswordHash());
            return ResponseEntity.ok(passwordMatches);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userRepository.deleteById(id);
            return new ResponseEntity<>("Deletado com sucesso!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("ID não encontrado!", HttpStatus.NOT_FOUND);
        }
    }
}
