package de.mpg.mpdl.auth.service.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.auth.model.UserAccount;
import de.mpg.mpdl.auth.model.security.LoginCredentials;
import de.mpg.mpdl.auth.repository.UserRepository;

@Service
public class LoginService {
	
	private UserRepository userRepository;
	
	public LoginService() {
		this(null);
	}
	
	@Autowired
	public LoginService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public Optional<UserAccount> login(LoginCredentials crredentials) {
		return userRepository.findByUserid(crredentials.getUserid())
				.filter(user -> user.getPassword().equals(crredentials.getPassword()));
	}

}
