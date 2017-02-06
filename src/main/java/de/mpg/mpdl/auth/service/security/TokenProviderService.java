package de.mpg.mpdl.auth.service.security;

import static java.time.ZoneOffset.UTC;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.auth.model.UserAccount;
import de.mpg.mpdl.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenProviderService {
	
	private static final String ISSUER = "inge.mpdl.mpg.de";
	private TokenKeyProvider tokenKeyProvider;
	private UserRepository userRepository;

	public TokenProviderService() {
		this(null, null);
	}
	
	@Autowired
	public TokenProviderService(TokenKeyProvider tokenKeyProvider, UserRepository userRepository) {
		this.tokenKeyProvider = tokenKeyProvider;
		this.userRepository = userRepository;
	}
	
	public String getToken(UserAccount user) throws IOException, URISyntaxException {
		byte[] key = tokenKeyProvider.getKey();
		Date expirationDate = Date.from(LocalDateTime.now().plusHours(2).toInstant(UTC));
		return Jwts.builder()
				.setSubject(user.getUserid())
				.setExpiration(expirationDate)
				.setIssuer(ISSUER)
				.signWith(SignatureAlgorithm.HS512, key)
				.compact();
	}
	
	public Optional<UserAccount> getUser(String token) throws IOException, URISyntaxException {
		byte[] key = tokenKeyProvider.getKey();
		Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
		return userRepository.findByUserid(claims.getBody().getSubject().toString());
	}
	
	public Date getExpirationDate(String token) throws IOException, URISyntaxException {
		byte[] key = tokenKeyProvider.getKey();
		Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
		return claims.getBody().getExpiration();
	}
}
