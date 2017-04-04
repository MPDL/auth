package de.mpg.mpdl.auth.model.dto;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import de.mpg.mpdl.auth.model.UserAccount;

@Projection(name = "minimal", types = UserAccount.class)
public interface UserAccountDTO {
	
	// String getFirstName();
	// String getLastName();
	@Value("#{target.userid}")
	String getUserid();
	@Value	("#{target.firstName} #{target.lastName}")
	String getFullName()	;
	@Value("#{target.ouid}")
	String getOuid();
	@Value("#{target.exid}")
	String getExid();
	@Value("#{target.email}")
	String getEmail();
	Set<GrantDTO> getGrants();

}
