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

	@Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.id = 1")
	List<User> getDoctors();

	@Query("SELECT DISTINCT u, c.conversationId " +
			"FROM User u " +
			"JOIN u.roles r " +
			"LEFT JOIN Conversation c ON (u.id = c.doctorId OR u.id = c.userId) " +
			"WHERE r.id = 1 AND (c.userId = :userId OR c.doctorId = :userId)")
	List<Object[]> getDoctorsWithConversationsByUserId(@Param("userId") UUID userId);
}
