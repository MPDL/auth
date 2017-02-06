package de.mpg.mpdl.auth.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ResponseBody
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public VndErrors handleAccesssDenied(Exception e, HttpServletRequest request) {
		VndErrors message = new VndErrors(e.getClass().getName(), e.getMessage(), new Link(request.getRequestURI()));
		return message;
	}
	
	@ResponseBody
	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public VndErrors handleUserNotFound(Exception e, HttpServletRequest request) {
		VndErrors message = new VndErrors(e.getClass().getName(), e.getMessage(), new Link(request.getRequestURI()));
		return message;
	}
	
	@ResponseBody
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public VndErrors handleServerError(Exception e, HttpServletRequest request) {
		VndErrors message = new VndErrors(e.getClass().getName(), e.getMessage(), new Link(request.getRequestURI()));
		return message;
	}
}
