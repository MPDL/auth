package de.mpg.mpdl.auth.model.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import de.mpg.mpdl.auth.model.Grant;

@Projection(name = "grantDTO", types = Grant.class)
public interface GrantDTO {
	
	String getTargetId();
	@Value("#{target.role.name}")
	String getRoleName();
	// UserRole getRole();

}
