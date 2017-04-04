package de.mpg.mpdl.auth.web.security;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.auth.exception.UserNotFoundException;
import de.mpg.mpdl.auth.model.UserAccount;
import de.mpg.mpdl.auth.model.security.AuthenticatedUser;
import de.mpg.mpdl.auth.model.security.AuthenticationToken;
import de.mpg.mpdl.auth.repository.UserRepository;
import de.mpg.mpdl.auth.service.security.TokenProviderService;

@Component
public class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
	
	@Autowired
	TokenProviderService tokenProvider;
	
	@Autowired
	UserRepository repo;
	
	@Override
	public boolean supports(Class<?> authentication) {
		return (AuthenticationToken.class.isAssignableFrom(authentication));
		//return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		
	}
	
	
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		AuthenticationToken authenticationToken = (AuthenticationToken) authentication;
		String token = authenticationToken.getToken();
		try {
			UserAccount user = tokenProvider.getUser(token).get();
			if (user == null) {
				throw new UserNotFoundException("Unable to get user from token");
			}
			List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
			user.getGrants().forEach(grant -> list.add(new SimpleGrantedAuthority("ROLE_"+grant.getRole().getName())));
			// System.out.println("TokenAuthenticationProvider: " + user.getUserid() + "  with ROLES:  " + list );
			return new AuthenticatedUser(user.getId(), user.getUserid(), token, list);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
}
