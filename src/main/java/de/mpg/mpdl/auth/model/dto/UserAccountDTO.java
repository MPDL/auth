package de.mpg.mpdl.auth.model.dto;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import de.mpg.mpdl.auth.model.UserAccount;

@Projection(name = "minimal", types = UserAccount.class)
public interface UserAccountDTO {
	
	// String getFirstName();
	// String getLastName();
	@Value	("#{target.firstName} #{target.lastName}")
	String getFullName()	;
	Set<GrantDTO> getGrants();

}
