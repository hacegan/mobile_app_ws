package com.app.ws.mobileappws.exceptions;

import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.app.ws.mobileappws.ui.model.response.ErrorMessage;

@ControllerAdvice // tüm projede exception handle etmek veya initbinder kullanmak istediğimizde bu
					// annotu kullanıyoruz.
public class AppExceptionsHandler {

	@ExceptionHandler(value = { UserServiceException.class }) // Exception fırlatacagı zaman buraya geliyor burada
																// handle ediliyor.
	public ResponseEntity<Object> handleUserServiceException(UserServiceException ex, WebRequest request) {

		ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());

		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);// ex.getmessage
																										// dersek
																										// sadece
																										// string ex
																										// dersek
																										// tüm objei
																										// döndürecek
	}

	@ExceptionHandler(value = { Exception.class })//Diğer tüm exceptionlar buraya geliyor.
	public ResponseEntity<Object> handleOtherExceptions(Exception ex, WebRequest request) {

		ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());

		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
