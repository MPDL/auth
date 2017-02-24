package de.mpg.mpdl.auth.web.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.auth.exception.UserNotFoundException;
import de.mpg.mpdl.auth.model.UserAccount;
import de.mpg.mpdl.auth.model.security.LoginCredentials;
import de.mpg.mpdl.auth.repository.UserRepository;
import de.mpg.mpdl.auth.service.security.LoginService;
import de.mpg.mpdl.auth.service.security.TokenProviderService;

@RestController
@RequestMapping(path = "/token")
public class TokenController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenController.class);
    
    @Autowired
    LoginService loginSevice;
    
    @Autowired
    TokenProviderService tokenProvider;
    
    @Autowired
    UserRepository repo;
    

    @RequestMapping(path = "", method = POST, produces = APPLICATION_JSON_VALUE)
    public UserAccount login(@RequestBody LoginCredentials credentials, HttpServletResponse response) {
    	
    	return loginSevice.login(credentials)
    			.map(user -> {
    				try {
    					// System.out.println("TokenController returns token 4: " + user.getUserid());
    					response.setHeader("Token", tokenProvider.getToken(user));
    				} catch(Exception e) {
    					throw new RuntimeException(e);
    				}
    				return user;
    			})
    			.orElseThrow(() -> new UserNotFoundException(credentials.getUserid()));
    }
    
    @RequestMapping(path = "/who")
    public ResponseEntity<UserAccount> getUser(@RequestHeader("Authorization") String token) {
    	
    	/*
    	final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            final String currentPrincipalName = authentication.getName();
            System.out.println("Authentication: " + authentication);
            System.out.println("Principal: " + authentication.getPrincipal());
            System.out.println(currentPrincipalName);
        }
        */
    	try {
    		return new ResponseEntity<UserAccount>(tokenProvider.getUser(token).get(), HttpStatus.OK);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    @RequestMapping(path = "/when")
    public ResponseEntity<Date> getExpirationDate(@RequestHeader("Authorization") String token) {
    	
    	try {
        	Date expirationDate = tokenProvider.getExpirationDate(token);
    		return new ResponseEntity<Date>(expirationDate, HttpStatus.OK);
    	} catch (IOException | URISyntaxException e) {
    		e.printStackTrace();
		}
		return null;
    }
}

