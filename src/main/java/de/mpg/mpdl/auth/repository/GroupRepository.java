package de.mpg.mpdl.auth.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.mpg.mpdl.auth.model.UserGroup;

@RepositoryRestResource(collectionResourceRel = "groups", path = "groups", exported = false)
public interface GroupRepository extends BaseJpaRepository<UserGroup, Long> {

}
