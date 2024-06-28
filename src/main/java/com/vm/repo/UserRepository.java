package com.vm.repo;

import com.vm.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
	@Query("SELECT u FROM User u WHERE u.username = :username")
	public User getUserByUsername(@Param("username") String username);

	@Query("SELECT u.id FROM User u WHERE u.username = :username")
	public UUID getUserIdByUsername(@Param("username") String username);

	@Query("SELECT u.publicKey FROM User u WHERE u.id = :userid")
	public String getPublicKeyByUserid(@Param("userid") UUID userid);

	@Query("SELECT u FROM User u WHERE u.username = :username")
	public List<User> getDoctors();
}
