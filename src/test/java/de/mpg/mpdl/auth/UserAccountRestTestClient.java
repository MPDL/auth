package de.mpg.mpdl.auth;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.mpg.mpdl.auth.model.UserRole;
import de.mpg.mpdl.auth.model.security.LoginCredentials;
import de.mpg.mpdl.auth.configuration.AuthConfiguration;
import de.mpg.mpdl.auth.model.Grant;
import de.mpg.mpdl.auth.model.UserAccount;

public class UserAccountRestTestClient {
	
	public static final String AUTH_URI = "http://localhost:8080/auth";
	
	public static PasswordEncoder encoder = new BCryptPasswordEncoder();
    
	private static HttpHeaders doAuthenticatedExchange(String user, String password) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		LoginCredentials creds = new LoginCredentials();
		creds.setUserid("boosen");
		creds.setPassword("boosen");
		HttpEntity<LoginCredentials> login = new HttpEntity<LoginCredentials>(creds, httpHeaders);
		ResponseEntity<Void> results = restTemplate.postForEntity(AUTH_URI+"/token", login, Void.class);

		httpHeaders.add("Authorization", results.getHeaders().getFirst("Token"));
		
		return httpHeaders;
	}

    /* GET */
    private static void listAllUsers(){
        System.out.println("Testing listAllUsers");
         
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders heads = doAuthenticatedExchange("boosen", "boosen");
        HttpEntity<String> request = new HttpEntity<>(heads);
        
        HttpEntity<String> response = restTemplate.exchange(AUTH_URI+"/users?short", HttpMethod.GET, request, String.class);
        System.out.println(response.getBody());
        /*
        List<LinkedHashMap<String, Object>> usersMap = restTemplate.getForObject(AUTH_URI+"/users/", List.class);
         
        if(usersMap!=null){
            for(LinkedHashMap<String, Object> map : usersMap){
                System.out.println("User : id="+map.get("userid")+", Grants="+map.get("grants"));;
            }
        }else{
            System.out.println("No users exis");
        }
        */
    }
    
    /* GET */
    private static void listAllAccounts(){
        System.out.println("Testing listAllAccounts");
         
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(AUTH_URI+"/rest/accounts/", String.class);
       
        if(response!=null){
        	ObjectMapper mapper = new ObjectMapper();
        	JsonNode root;
			try {
				root = mapper.readTree(response.getBytes());
				JsonNode accounts = root.findValue("accounts");
				if (accounts.isArray()) {
				    for (final JsonNode objNode : accounts) {
				        System.out.println(objNode);
				    }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }else{
            System.out.println("No users exis");
        }
    }
     
    /* GET */
    private static void getUser(String userid){
        System.out.println("Testing getUser API----------");
        RestTemplate restTemplate = new RestTemplate();
        UserAccount user = restTemplate.getForObject(AUTH_URI+"/users/"+userid, UserAccount.class);
        System.out.println(user.getUserid());
    }
     
    /* POST */
    private static void createUser() {
        System.out.println("Testing create User API----------");
        
        RestTemplate restTemplate = new RestTemplate();
        Set<Grant> grants = new HashSet<Grant>();
        Grant grant = new Grant(new UserRole("DEPOSITOR"), Grant.TargetType.CONTEXT, "ctx_id");
        grants.add(grant);
        
        UserAccount user = UserAccount.getBuilder()
        		.active(true)
        		.email("siedersleben@mpdl.mpg.de")
        		.ouid("vm44.mpdl.mpg.de/inge/organizational_units/organization/pure_persistent25")
        		.firstName("Gudrun")
        		.lastName("Siedersleben")
        		.password(encoder.encode("gudrun"))
        		.grants(grants)
        		.userid("siedersleben").build();
        user.setId(null);
        
        /*
        		MultiValueMap<String, String> headers =
        		new LinkedMultiValueMap<String, String>();
        		headers.add("Content-Type", "application/json"); //Note Content-Type as opposed to Accept
        */	
        		HttpHeaders headers = new HttpHeaders();
        		headers.setContentType(MediaType.APPLICATION_JSON);

        		HttpEntity<Object> entity = new HttpEntity<Object>(user, headers);
        		try {
        			/*
        		ResponseEntity<UserAccount> response = restTemplate.exchange(
        		"http://localhost:8080/auth/users",	HttpMethod.POST, entity, UserAccount.class); 
        		*/
        		UserAccount created = restTemplate.postForObject(AUTH_URI + "/users", entity, UserAccount.class);
                System.out.println("Location : "+created);

        		} catch (RestClientException e) {
        			e.printStackTrace();
        		}
       // URI uri = restTemplate.postForLocation(AUTH_URI+"/users", user, UserAccount.class);
    }
 
    /* PUT */
    private static void updateUser() {
        System.out.println("Testing update User API----------");
        RestTemplate restTemplate = new RestTemplate();

        UserAccount user = UserAccount.getBuilder()
        		.active(true)
        		.email("testuser@mpdl.mpg.de")
        		.firstName("test")
        		.lastName("married_user")
        		.password(encoder.encode("newpwd"))
        		.userid("testuser").build();
        restTemplate.put(AUTH_URI+"/users/testuser", user);
        System.out.println(user.getLastName());
    }
 
    /* DELETE */
    private static void deleteUser() {
        System.out.println("Testing delete User API----------");
        RestTemplate restTemplate = new RestTemplate();
        UserAccount user = restTemplate.getForObject(AUTH_URI+"/users/testuser", UserAccount.class);
        Long id = user.getId();
        restTemplate.delete(AUTH_URI+"/users/"+id);
    }
 
 
 
    public static void main(String args[]){
        
        createUser();
        listAllUsers();
        //listAllAccounts();
        testPatch();
       }
    
    public static void testProps() {
    	
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(AuthConfiguration.class);
    	Environment env = ctx.getEnvironment();
    	String moderator = env.getProperty("test.moderator.name");
    	System.out.println("and the moderator is " + moderator);
    	
    }
    
    public static void testPatch() {
    	
    	UserAccount user = UserAccount.getBuilder().password("newPassword").build();
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    	mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    	mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    	try {
			mapper.writeValue(System.out, user);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

}
