package de.mpg.mpdl.auth.web.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import de.mpg.mpdl.auth.exception.UserNotFoundException;
import de.mpg.mpdl.auth.model.security.AuthenticationToken;
import de.mpg.mpdl.auth.service.security.TokenProviderService;

public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	
	@Autowired
	TokenProviderService service;
	
	public TokenAuthenticationFilter() {
		super("/**");
	}
	
	TokenAuthenticationSuccessHandler success = new TokenAuthenticationSuccessHandler();
	
	@Override
	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		return true;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		String header = request.getHeader("Authorization");
		if (header == null) {
			throw new UserNotFoundException("No authorization token found.");
		}
		AuthenticationToken token = new AuthenticationToken(header);
		return getAuthenticationManager().authenticate(token);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);
		// System.out.println("TokenAuthenticationFilter success " + authResult.isAuthenticated());
		
		chain.doFilter(request, response);
	}
}
