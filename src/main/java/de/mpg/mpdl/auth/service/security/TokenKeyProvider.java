package de.mpg.mpdl.auth.service.security;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

@Component
public class TokenKeyProvider {
	
	public byte[] getKey() throws URISyntaxException, IOException {
		return Files.readAllBytes(Paths.get(this.getClass().getResource("/jwt.key").toURI()));
	}

}
