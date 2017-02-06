package de.mpg.mpdl.auth.repository;

import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.mpg.mpdl.auth.model.UserRole;

@RepositoryRestResource(collectionResourceRel = "roles", itemResourceRel = "role", path = "roles")
public interface RoleRepository extends BaseJpaRepository<UserRole, Long> {

	Optional<UserRole> findByName(@Param("name") String name);

}
