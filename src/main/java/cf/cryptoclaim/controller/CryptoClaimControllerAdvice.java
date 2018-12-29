package cf.cryptoclaim.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import cf.cryptoclaim.exception.AuthenticationFailedException;
import cf.cryptoclaim.exception.WrongPasswordException;

@ControllerAdvice(basePackages = "cf.cryptoclaim.controller")
public class CryptoClaimControllerAdvice {

	 @ExceptionHandler(AuthenticationFailedException.class)
	  protected ResponseEntity<?> authenticationFailedException(AuthenticationFailedException ex, HttpServletRequest request) {
		 return ResponseEntity.status(401).body(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
	  }
	
	 @ExceptionHandler(WrongPasswordException.class)
	  protected ResponseEntity<?> wrongPasswordException(WrongPasswordException ex, HttpServletRequest request) {
		 return ResponseEntity.status(401).body(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
	  }
	 
	 @ExceptionHandler(Exception.class)
	  protected ResponseEntity<?> generalException(Exception ex, HttpServletRequest request) {
		 return ResponseEntity.status(500).body(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
	  }
}
