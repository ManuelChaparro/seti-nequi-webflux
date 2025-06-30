package com.seti.webflux_test.infraestructure.entrypoint.web.exception;

public class CustomException extends Exception{
    
    public CustomException(String msg){
        super(msg);
    }
}
