package de.mpg.mpdl.auth.service.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.auth.model.UserAccount;
import de.mpg.mpdl.auth.model.security.LoginCredentials;
import de.mpg.mpdl.auth.repository.UserRepository;

@Service
public class LoginService {
	
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	
	public LoginService() {
		this(null, null);
	}
	
	@Autowired
	public LoginService(UserRepository userRepository, PasswordEncoder encoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = encoder;
	}
	
	public Optional<UserAccount> login(LoginCredentials credentials) {
		return userRepository.findByUserid(credentials.getUserid())
				.filter(user -> passwordEncoder.matches(credentials.getPassword(), user.getPassword()));
	}

}
