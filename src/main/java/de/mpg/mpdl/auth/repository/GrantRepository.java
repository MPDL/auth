package de.mpg.mpdl.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.mpg.mpdl.auth.model.Grant;
import de.mpg.mpdl.auth.model.UserRole;

@RepositoryRestResource(collectionResourceRel = "grants", itemResourceRel = " grant", path = "grants")
public interface GrantRepository extends BaseJpaRepository<Grant, Long> {

	@Query(value = "SELECT * FROM grants g WHERE " +
            "g.role = :role AND " +
            "LOWER(g.target_id) = LOWER(CONCAT('%',:target, '%'))",
            nativeQuery = true
    )
	Optional<Grant> find(@Param("role") int role, @Param("target") String target);
	
	@Query("SELECT g FROM Grant g WHERE " +
            "LOWER(g.role.name) LIKE LOWER(CONCAT('%',:role, '%')) AND " +
            "LOWER(g.targetId) LIKE LOWER(CONCAT('%',:target, '%'))")
	Optional<Grant> findExisting(@Param("role") String role, @Param("target") String target);
}
