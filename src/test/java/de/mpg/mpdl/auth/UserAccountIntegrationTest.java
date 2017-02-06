/*
package de.mpg.mpdl.auth;

import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.mpg.mpdl.auth.configuration.TestConfiguration;
import de.mpg.mpdl.auth.exception.UserNotFoundException;
import de.mpg.mpdl.auth.model.UserAccount;
import de.mpg.mpdl.auth.model.dto.UserAccountDTO;
import de.mpg.mpdl.auth.model.security.LoginCredentials;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserAccountIntegrationTest {
	
    private static final String USER_URI = "http://localhost:8080/auth/users";
    private static final String TOKEN_URI = "http://localhost:8080/auth/token";
    private static final String UNKNOWN = "unknown";
    private static final String DELETE_URI = "http://localhost:8080/auth/rest/accounts";

    
    @Value("${test.moderator.name}")
    private String moderatorUserid;
    @Value("${test.moderator.pwd}")
    private String moderatorPassword;
    @Value("${test.administrator.name}")
    private String administratorUserid;
    @Value("${test.administrator.pwd}")
    private String administratorPassword;
    
    @Autowired
    private RestTemplate template;
    
    private String moderatorToken;
    private String administratorToken;
    /*
    @Before
    public void getToken4ModeratorAndAdmin() {
    	HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		LoginCredentials creds = new LoginCredentials();
		creds.setUserid(moderatorUserid);
		creds.setPassword(moderatorPassword);
		HttpEntity<LoginCredentials> login = new HttpEntity<LoginCredentials>(creds, httpHeaders);
		ResponseEntity<Void> results = template.postForEntity(TOKEN_URI, login, Void.class);
		this.moderatorToken = results.getHeaders().getFirst("Token");
		creds.setUserid(administratorUserid);
		creds.setPassword(administratorPassword);
		login = new HttpEntity<LoginCredentials>(creds, httpHeaders);
		results = template.postForEntity(TOKEN_URI, login, Void.class);
		this.administratorToken = results.getHeaders().getFirst("Token");
    }
    
    private HttpHeaders doAuthenticatedExchange(String token) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		httpHeaders.add("Authorization", token);
		
		return httpHeaders;
	}
    
    @Test
    public void testAgetShortListAsModerator() {
    	System.out.println("starting test A");
    	HttpHeaders heads = doAuthenticatedExchange(this.moderatorToken);
    	HttpEntity<String> request = new HttpEntity<>(heads);
        
        ResponseEntity<String> response = template.exchange(USER_URI + "?short", HttpMethod.GET, request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        
    }
    
    @Test//(expected = HttpClientErrorException.class)
    public void testBgetLongListAsModerator() {
    	System.out.println("starting test B");
    	HttpHeaders heads = doAuthenticatedExchange(this.moderatorToken);
    	HttpEntity<String> request = new HttpEntity<>(heads);
    	ResponseEntity<String> response = null;
    	try {
            response = template.exchange(USER_URI, HttpMethod.GET, request, String.class);
    	} catch (HttpClientErrorException e) {
    		assertThat(e.getStatusCode(), is(HttpStatus.FORBIDDEN));
    	}
    }
    
    @Test
    public void testCgetLongListAsAdministrator() {
    	System.out.println("starting test C");

    	HttpHeaders heads = doAuthenticatedExchange(this.administratorToken);
    	HttpEntity<String> request = new HttpEntity<>(heads);
        
        ResponseEntity<String> response = template.exchange(USER_URI, HttpMethod.GET, request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        
    }
    
    @Test
    public void testDgetModeratorUserAccount() {
    	System.out.println("starting test D");

    	HttpHeaders heads = doAuthenticatedExchange(this.moderatorToken);
    	HttpEntity<String> request = new HttpEntity<>(heads);
        
        ResponseEntity<UserAccount> response = template.exchange(USER_URI + "/" + moderatorUserid, HttpMethod.GET, request, UserAccount.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getUserid(), is(moderatorUserid));
    }
    
    @Test
    public void testEgetUnknownUserAccount() {
    	System.out.println("starting test E");

    	HttpHeaders heads = doAuthenticatedExchange(this.administratorToken);
    	HttpEntity<String> request = new HttpEntity<>(heads);
    	ResponseEntity<UserAccount> response = null;
    	try {
    		response  = template.exchange(USER_URI + "/" + UNKNOWN, HttpMethod.GET, request, UserAccount.class);
    	} catch (HttpClientErrorException e) {
    		assertThat(e.getStatusCode(), is(HttpStatus.NOT_FOUND));
    	}
    }
    
    @Test
    public void testFgetAdministratorUserAccountAsModerator() {
    	System.out.println("starting test F");

    	HttpHeaders heads = doAuthenticatedExchange(this.moderatorToken);
    	HttpEntity<String> request = new HttpEntity<>(heads);
    	ResponseEntity<UserAccount> response = null;
    	try {
            response = template.exchange(USER_URI + "/" + administratorUserid, HttpMethod.GET, request, UserAccount.class);
    	} catch (HttpClientErrorException e) {
    		assertThat(e.getStatusCode(), is(HttpStatus.FORBIDDEN));
    	}
    }
    /*
    @Test
    public void testGcreateNewUserAsModerator() {
    	System.out.println("starting test G");

    	HttpHeaders heads = doAuthenticatedExchange(this.moderatorToken);
    	UserAccount user = new TestData().getTestUserDepositor();
    	HttpEntity<UserAccount> request = new HttpEntity<>(user, heads);

    	URI response = null;
    	try {
            response = template.postForLocation(USER_URI, request);
    	} catch (HttpClientErrorException e) {
    		assertThat(e.getStatusCode(), is(HttpStatus.FORBIDDEN));
    	}
    }
    */
    /*
    @Test
    public void testHcreateNewUserAsAdministrator() {
    	System.out.println("starting test H");

    	HttpHeaders heads = doAuthenticatedExchange(this.administratorToken);
		heads.setContentType(MediaType.APPLICATION_JSON);
    	UserAccount user = new TestData().getTestUserDepositor();
    	HttpEntity<UserAccount> request = new HttpEntity<>(user, heads);
    	ResponseEntity<UserAccount> response = null;
        	response =  template.postForEntity(USER_URI, request, UserAccount.class);
        	assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        	assertThat(response.getHeaders().getLocation().toString(), endsWith("testDepositor"));
    }
    
    @Test
    public void testIcreateExistingUserAsAdministrator() {
    	System.out.println("starting test I");

    	HttpHeaders heads = doAuthenticatedExchange(this.administratorToken);
		heads.setContentType(MediaType.APPLICATION_JSON);
    	UserAccount user = new TestData().getTestUserDepositor();
    	HttpEntity<UserAccount> request = new HttpEntity<>(user, heads);
    	ResponseEntity<UserAccount> response = null;
    	try {
        	response =  template.postForEntity(USER_URI, request, UserAccount.class);
    	} catch (HttpClientErrorException e) {
        	assertThat(e.getStatusCode(), is(HttpStatus.CONFLICT));
    	}
    }
    */
    /*
    @Test
    public void testJupdateUserAccount() {
    	System.out.println("starting test J");

    	HttpHeaders heads = doAuthenticatedExchange(this.administratorToken);
    	HttpEntity<String> request = new HttpEntity<>(heads);
        
        ResponseEntity<UserAccount> response = template.exchange(USER_URI + "/" + "testDepositor", HttpMethod.GET, request, UserAccount.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        UserAccount user = response.getBody();
		heads.setContentType(MediaType.APPLICATION_JSON);

		try {
			new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValue(System.out, user);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        HttpEntity<UserAccount> updateRequest = new HttpEntity<UserAccount>(user, heads);
        response = template.exchange(USER_URI + "/" + "testDepositor", HttpMethod.PUT, updateRequest, UserAccount.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }
    */
    /*
    @Test
    public void testKdeleteExistingUserAsAdministrator() {
    	System.out.println("starting test K");

    	HttpHeaders heads = doAuthenticatedExchange(this.administratorToken);
    	HttpEntity<String> request = new HttpEntity<>(heads);
        
        ResponseEntity<UserAccount> response = template.exchange(USER_URI + "/testDepositor", HttpMethod.GET, request, UserAccount.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        
        response = template.exchange(DELETE_URI +"/" + response.getBody().getId().toString(), HttpMethod.DELETE, request, UserAccount.class);
        assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }
	*/
/*
	public String getModeratorToken() {
		return moderatorToken;
	}

	public void setModeratorToken(String moderatorToken) {
		this.moderatorToken = moderatorToken;
	}

	public String getAdministratorToken() {
		return administratorToken;
	}

	public void setAdministratorToken(String administratorToken) {
		this.administratorToken = administratorToken;
	}

	

}
*/
