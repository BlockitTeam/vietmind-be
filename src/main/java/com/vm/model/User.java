package com.vm.model;

import com.vm.constant.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
	private UUID id;

	private String username;
	private String password;
	private boolean enabled = false;

	private boolean surveyCompleted = false;
	
	@Enumerated(EnumType.STRING)
	private Provider provider;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "birth_year")
	private Integer birthYear;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender")
	private Gender gender;

	@Column(name = "public_key", columnDefinition = "TEXT", nullable = false)
	private String publicKey;

	@Column(name = "encrypted_private_key", columnDefinition = "TEXT", nullable = false)
	private String encryptedPrivateKey;

	@Getter
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<Role> roles = new HashSet<>();
}