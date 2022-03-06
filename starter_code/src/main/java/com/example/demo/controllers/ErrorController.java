package com.example.demo.controllers;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Implements the Error controller related to any errors handled by the Vehicles API
 */
@ControllerAdvice
public class ErrorController{

	private static final String ENTITY_NOT_FOUND_EXCEPTION_MESSAGE = "Entity Not Found Exception: ";

	private static final String USERNAME_NOT_FOUND_EXCEPTION_MESSAGE = "User Not Found Exception - for username : ";

	Logger logger = LoggerFactory.getLogger(getClass());

	@ExceptionHandler(value = EntityNotFoundException.class)
	public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
		logger.error(ENTITY_NOT_FOUND_EXCEPTION_MESSAGE + ex.getMessage());
		return new ResponseEntity<Object>(ex.getMessage(),HttpStatus.NOT_FOUND);
	} 
	
	@ExceptionHandler(value = UsernameNotFoundException.class)
	public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
		logger.error(USERNAME_NOT_FOUND_EXCEPTION_MESSAGE + ex.getMessage());
		return new ResponseEntity<Object>(ex.getMessage(),HttpStatus.NOT_FOUND);
	} 
}

