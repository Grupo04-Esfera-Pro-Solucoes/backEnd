package com.esfera.g2.esferag2.controller.exeptions;

import com.esfera.g2.esferag2.controller.requests.UserRegistrationRequest;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ResponseStatus
public class UserRegistrationExeption extends RuntimeException{
    public UserRegistrationExeption(String message){
        super(message);
    }
}
