package com.esfera.g2.esferag2.controller.exeptions;

public class UserRegistrationExeption extends RuntimeException{
    public UserRegistrationExeption(String message){
        super(message);
    }
}
