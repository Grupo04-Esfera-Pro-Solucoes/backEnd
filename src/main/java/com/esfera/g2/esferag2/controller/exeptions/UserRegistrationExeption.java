package com.esfera.g2.esferag2.controller.exeptions;

import com.esfera.g2.esferag2.controller.requests.UserRegistrationRequest;

public class UserRegistrationExeption extends RuntimeException{
    public UserRegistrationExeption(String message){
        super(message);
    }
}
