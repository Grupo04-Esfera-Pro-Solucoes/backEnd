package com.esfera.g2.esferag2.controller;

import com.esfera.g2.esferag2.controller.requests.UserRegistrationRequest;
import com.esfera.g2.esferag2.model.User;
import com.esfera.g2.esferag2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
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
            redirectAttributes.addFlashAttribute("error", "O email de usuário já está sendo usado");
            return "redirect:/register";
        }

        if (name.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty() || role.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Todos os campos devem ser preenchidos");
            return "redirect:/register";
        }

        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            redirectAttributes.addFlashAttribute("error", "Formato de e-mail inválido");
            return "redirect:/register";
        }

        if (!phone.matches("\\d{10,11}")) {
            redirectAttributes.addFlashAttribute("error", "Formato de telefone inválido");
            return "redirect:/register";
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setPasswordHash(password);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setRole(role);
        userService.save(newUser);

        redirectAttributes.addFlashAttribute("success", "Usuário registrado com sucesso");
        return "redirect:/login";
    }


    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            RedirectAttributes redirectAttributes) {
        User user = null;
        if (email.isEmpty() || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Todos os campos devem ser preenchidos");
            return "redirect:/login";
        } else {

            user = userService.findByEmail(email);
        }
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Usuário não encontrado");
            return "redirect:/login";
        } else if (!userService.checkPassword(password, user.getPasswordHash())) {
            redirectAttributes.addFlashAttribute("error", "Senha incorreta");
            return "redirect:/login";
        } else {

            redirectAttributes.addAttribute("userId", user.getIdUser());

            return "redirect:/nav/dashboard";
        }
    }
}
