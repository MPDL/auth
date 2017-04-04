package de.mpg.mpdl.auth.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.mpg.mpdl.auth.exception.UserNotFoundException;
import de.mpg.mpdl.auth.model.UserAccount;
import de.mpg.mpdl.auth.model.dto.UserAccountDTO;
import de.mpg.mpdl.auth.repository.UserRepository;

@RestController
@RequestMapping(value="/users")
public class UserAccountController {
	
	private UserRepository repo;
	
	private ProjectionFactory projectionFactory;
	
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	public UserAccountController(UserRepository repo, ProjectionFactory factory, PasswordEncoder encoder) {
		this.repo = repo;
		this.projectionFactory = factory;
		this.passwordEncoder = encoder;
	}
	
	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> add(@RequestBody UserAccount input) {
		if (repo.findByUserid(input.getUserid()).isPresent()) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		String encoded = passwordEncoder.encode(input.getPassword());
		input.setPassword(encoded);
		UserAccount user = this.repo.save(input);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(user.getUserid()).toUri());
		return new ResponseEntity<>(null, headers, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.GET, params = {"short"})
	public ResponseEntity<List<UserAccountDTO>> shortList(@RequestParam("short") String mini) {
		List<UserAccount> list = repo.findAll();
		if ( list.isEmpty()) {
			return new ResponseEntity<List<UserAccountDTO>>(HttpStatus.NO_CONTENT);
		}
		List<UserAccountDTO>  list2return = list.stream().map(user -> projectionFactory.createProjection(UserAccountDTO.class, user)).collect(Collectors.toList());
		return new ResponseEntity<List<UserAccountDTO>>(list2return, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<UserAccount>> list() {
		List<UserAccount> list = repo.findAll();
		if ( list.isEmpty()) {
			return new ResponseEntity<List<UserAccount>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<UserAccount>>(list, HttpStatus.OK);
	}
	
    @PreAuthorize ("hasRole('ROLE_SYSADMIN') or #userId == authentication.name")
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public ResponseEntity<UserAccount> list(@PathVariable("userId") String userId, Authentication auth) {
		this.validateUser(userId);
		return new ResponseEntity<UserAccount>(repo.findByUserid(userId).get(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody UserAccount input) {
		this.validateUser(input.getUserid());
		String encoded = passwordEncoder.encode(input.getPassword());
		input.setPassword(encoded);
		UserAccount user = this.repo.save(input);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(user.getUserid()).toUri());
		return new ResponseEntity<>(null, headers, HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<UserAccount> delete(@PathVariable Long id) {
		UserAccount user = repo.findOne(id);
		if (user == null) {
			return new ResponseEntity<UserAccount>(HttpStatus.NOT_FOUND);
		}
		repo.delete(id);
		return new ResponseEntity<UserAccount>(HttpStatus.GONE);
	}
	
	private void validateUser(String userId) {
		this.repo.findByUserid(userId).orElseThrow(
				() -> new UserNotFoundException(userId));
	}

}
