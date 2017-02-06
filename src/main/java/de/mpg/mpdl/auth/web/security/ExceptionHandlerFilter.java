package de.mpg.mpdl.auth.web.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.auth.exception.ErrorMessage;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		try {
			chain.doFilter(request, response);
		} catch (RuntimeException e){
			VndErrors message = new VndErrors(e.getClass().getName(), e.getMessage());
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.getWriter().write(convertMessage(message));
		}
	}
	
	public String convertMessage(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

}
