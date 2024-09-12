package com.esfera.g2.esferag2.controller;

import com.esfera.g2.esferag2.controller.exeptions.UserRegistrationExeption;
import com.esfera.g2.esferag2.controller.requests.UserRegistrationRequest;
import com.esfera.g2.esferag2.model.User;
import com.esfera.g2.esferag2.repository.UserRepository;
import com.esfera.g2.esferag2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public ModelAndView showLoginForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView showRegistrationForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody UserRegistrationRequest userRequest,
                               RedirectAttributes redirectAttributes) {

        String name = userRequest.getUsername();
        String password = userRequest.getPassword();
        String email = userRequest.getEmail();
        String phone = userRequest.getPhone();
        String role = userRequest.getRole();

        if (userService.findByEmail(email) != null) {
            throw new UserRegistrationExeption("O email de usuário já esta sendo usado");
        }

        if (name.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty() || role.isEmpty()) {
            throw new UserRegistrationExeption("Todos os campos devem ser preenchidos");
        }

        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            throw new UserRegistrationExeption("Formato de e-mail inválido");
        }

        if (!phone.matches("\\d{10,11}")) {
            throw new UserRegistrationExeption("Formato de telefone inválido");
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setPasswordHash(password);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setRole(role);
        userService.save(newUser);

        return "ok";
    }


    @PostMapping("/login")
    public User loginUser(@RequestBody UserRegistrationRequest userRequest,
                            RedirectAttributes redirectAttributes) {
        String email = userRequest.getEmail();
        String password = userRequest.getPassword();
        User user = null;

        if (email.isEmpty() || password.isEmpty()) {
            throw new UserRegistrationExeption("Todos os campos devem ser preenchidos");
        } else {
            user = userService.findByEmail(email);
        }
        if (user == null) {
            throw new UserRegistrationExeption("Usuário não encontrado");
        } else if (!userService.checkPassword(password, user.getPasswordHash())) {
            throw new UserRegistrationExeption("Senha incorreta");
        } else {
            return user;
        }
    }
}
