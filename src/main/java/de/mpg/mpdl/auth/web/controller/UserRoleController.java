package de.mpg.mpdl.auth.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.mpg.mpdl.auth.exception.UserNotFoundException;
import de.mpg.mpdl.auth.model.UserRole;
import de.mpg.mpdl.auth.repository.RoleRepository;

@RestController
@RequestMapping(value="/userroles")
public class UserRoleController {
	
	private RoleRepository repo;
	
	@Autowired
	UserRoleController(RoleRepository repo) {
		this.repo = repo;
	}
	
	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> add(@RequestBody UserRole input) {
		
		if (repo.findByName(input.getName()).isPresent()) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		UserRole role = this.repo.save(input);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(role.getName()).toUri());
		return new ResponseEntity<>(null, headers, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<UserRole>> list() {
		List<UserRole> list = repo.findAll();
		if ( list.isEmpty()) {
			return new ResponseEntity<List<UserRole>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<UserRole>>(list, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{roleName}", method = RequestMethod.GET)
	public ResponseEntity<UserRole> list(@PathVariable String roleName, Authentication auth) {
		return new ResponseEntity<UserRole>(repo.findByName(roleName).get(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable Long id) {
		UserRole userRole = repo.findOne(id);
		if (userRole == null) {
			return new ResponseEntity<UserRole>(HttpStatus.NOT_FOUND);
		}
		repo.delete(id);
		return new ResponseEntity<>(HttpStatus.GONE);
	}
	
}
