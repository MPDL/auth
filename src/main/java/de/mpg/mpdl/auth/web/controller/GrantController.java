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

import de.mpg.mpdl.auth.model.Grant;
import de.mpg.mpdl.auth.repository.GrantRepository;

@RestController
@RequestMapping(value="/usergrants")
public class GrantController {
	
	private GrantRepository repo;
	
	@Autowired
	GrantController(GrantRepository repo) {
		this.repo = repo;
	}
	
	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> add(@RequestBody Grant input) {
		
		Grant grant = this.repo.save(input);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(grant.getId()).toUri());
		return new ResponseEntity<>(null, headers, HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Grant>> list() {
		List<Grant> list = repo.findAll();
		if ( list.isEmpty()) {
			return new ResponseEntity<List<Grant>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Grant>>(list, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{grantId}", method = RequestMethod.GET)
	public ResponseEntity<Grant> list(@PathVariable String grantId, Authentication auth) {
		return new ResponseEntity<Grant>(repo.findOne(Long.valueOf(grantId)), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ROLE_SYSADMIN')")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Grant> delete(@PathVariable Long id) {
		Grant grant = repo.findOne(id);
		if (grant == null) {
			return new ResponseEntity<Grant>(HttpStatus.NOT_FOUND);
		}
		repo.delete(id);
		return new ResponseEntity<Grant>(HttpStatus.GONE);
	}

}
