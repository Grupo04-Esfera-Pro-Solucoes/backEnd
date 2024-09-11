package com.esfera.g2.esferag2.service;

import com.esfera.g2.esferag2.component.PasswordEncoderUtil;
import com.esfera.g2.esferag2.model.User;
import com.esfera.g2.esferag2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoderUtil passwordEncoderUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoderUtil passwordEncoderUtil) {
        this.userRepository = userRepository;
        this.passwordEncoderUtil = passwordEncoderUtil;
    }

    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        String hashedPassword = passwordEncoderUtil.encodePassword(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);
        return userRepository.save(user);
    }

    public String hashPassword(String password) {
        return passwordEncoderUtil.encodePassword(password);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoderUtil.checkPassword(rawPassword, encodedPassword);
    }
}
