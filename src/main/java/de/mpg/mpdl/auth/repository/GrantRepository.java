package de.mpg.mpdl.auth.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.mpg.mpdl.auth.model.Grant;

@RepositoryRestResource(collectionResourceRel = "grants", itemResourceRel = " grant", path = "grants")
public interface GrantRepository extends BaseJpaRepository<Grant, Long> {

}
