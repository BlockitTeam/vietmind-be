package com.vm.model;

import com.vm.constant.Provider;
import com.vm.enums.Gender;
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

	@Column(name = "survey_detail_id")
	private Integer surveyDetailId;

	@Column(name = "latest_specialized_version")
	private Integer latestSpecializedVersion;
	
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

	@Column(name = "workplace")
	private String workplace;

	// Field mới: Bằng cấp
	@Column(name = "degree")
	private String degree;

	// Field mới: Chuyên ngành (lưu danh sách survey_id dưới dạng chuỗi phân tách)
	@Column(name = "specializations")
	private String specializations; // e.g., "2,3,4" cho nhiều survey_id

	@Getter
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<Role> roles = new HashSet<>();

	public boolean hasRoleDoctor() {
		return roles.stream().anyMatch(role -> "ROLE_DOCTOR".equals(role.getName()));
	}
}