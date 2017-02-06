package de.mpg.mpdl.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.mpg.mpdl.auth.model.UserAccount;
import de.mpg.mpdl.auth.model.dto.UserAccountDTO;

@RepositoryRestResource(collectionResourceRel = "accounts", itemResourceRel = "account",  path = "accounts", excerptProjection = UserAccountDTO.class)
public interface UserRepository extends BaseJpaRepository<UserAccount, Long> {
	
	Optional<UserAccount> findByUserid(@Param("userId") String userid);
	
	@Query("SELECT u FROM UserAccount u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%',:searchTerm, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%',:searchTerm, '%'))")
    List<UserAccount> findBySearchTerm(@Param("searchTerm") String searchTerm);
	
	@Query(value = "SELECT * FROM users u WHERE " +
            "LOWER(u.first_name) LIKE LOWER(CONCAT('%',:searchTerm, '%')) OR " +
            "LOWER(u.last_name) LIKE LOWER(CONCAT('%',:searchTerm, '%'))",
            nativeQuery = true
    )
    List<UserAccount> findBySearchTermNative(@Param("searchTerm") String searchTerm);

}
