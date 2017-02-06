package de.mpg.mpdl.auth.service;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class UserAuditorAware implements AuditorAware<String> {

	@Override
	public String getCurrentAuditor() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return "n/a";
		}
		return ((UserDetails) authentication.getPrincipal()).getUsername();
	}

}
