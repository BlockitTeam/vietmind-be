package com.vm.service;

import com.vm.constant.Provider;
import com.vm.model.Role;
import com.vm.model.User;
import com.vm.repo.RoleRepository;
import com.vm.repo.UserRepository;
import com.vm.request.DoctorUserRequest;
import com.vm.request.UserRequest;
import com.vm.service.impl.CustomOAuth2User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public void processOAuthPostLogin(String username, Provider provider) throws Exception {
		User existUser = userRepo.getUserByUsername(username);
		if (existUser == null) {
			User newUser = new User();
			newUser.setUsername(username);
			newUser.setProvider(provider);
			Role roleUser = roleRepo.findByName("ROLE_USER")
					.orElseGet(() -> {
						Role newRole = new Role();
						newRole.setName("ROLE_USER");
						return roleRepo.save(newRole);
					});
			newUser.getRoles().add(roleUser);
			userRepo.save(newUser);
			System.out.println("Created new user: " + username);
		}
	}

	public User getCurrentUser(String username) {
		return userRepo.getUserByUsername(username);
	}

	public List<User> getDoctors() {
		return userRepo.getDoctors();
	}

	// Lấy bác sĩ cụ thể theo user_id
	public Optional<User> getDoctorById(String userId) {
		// Gọi getDoctors() rồi lọc theo userId
		return getDoctors().stream()
				.filter(doctor -> doctor.getId().toString().equals(userId))
				.findFirst();
	}

//	public List<UserDTO> getDoctorsWithConversations(UUID userId) {
//		List<Object[]> results = userRepo.getDoctorsWithConversationsByUserId(userId);
//
//		return results.stream()
//				.map(result -> {
//					User user = (User) result[0];
//					Integer conversationId = (Integer) result[1];
//					UserDTO userDTO = modelMapper.map(user, UserDTO.class);
//					userDTO.setConversationId(conversationId);
//					return userDTO;
//				})
//				.collect(Collectors.toList());
//	}

	public User update(UserRequest request, String userName) {
		User originUser = userRepo.getUserByUsername(userName);
		originUser.setFirstName(request.getFirstName());
		originUser.setLastName(request.getLastName());
		originUser.setBirthYear(request.getBirthYear());
		originUser.setGender(request.getGender());

		//Enable user after finish complete form register
		originUser.setEnabled(true);
		return userRepo.save(originUser);
	}

	public User updateUserDoctor(DoctorUserRequest request, String userName) {
		User originUser = userRepo.getUserByUsername(userName);
		originUser.setFirstName(request.getFirstName());
		originUser.setLastName(request.getLastName());
		originUser.setBirthYear(request.getBirthYear());
		originUser.setGender(request.getGender());
		originUser.setWorkplace(request.getWorkplace());
		originUser.setDegree(request.getDegree());
		originUser.setSpecializations(request.getSpecializations());

		return userRepo.save(originUser);
	}

	public String getCurrentUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = null;
		if (authentication != null) {
			Object principal = authentication.getPrincipal();

			if (principal instanceof UserDetails) {
				username = ((UserDetails) principal).getUsername();
			} else if (principal instanceof CustomOAuth2User) {
				username = ((CustomOAuth2User) principal).getEmail();
			} else if (principal instanceof DefaultOAuth2User) {
				username = ((DefaultOAuth2User) principal).getAttributes().get("email").toString();
			}else {
				username = principal.toString();
			}
		}
		return username;
	}

	public UUID getCurrentUUID() {
		String userName = getCurrentUserName();
		return userRepo.getUserIdByUsername(userName);
	}

	public String getStringCurrentUserId() {
		return getCurrentUUID().toString();
	}

	public void markCompleteGeneralSurvey(boolean surveyCompleted) {
		UUID currentUserId = getCurrentUUID();
		User user = userRepo.findById(currentUserId).get();
		user.setSurveyCompleted(surveyCompleted);
		userRepo.save(user);
	}

	public void clearInforSurveyDetail() {
		UUID currentUserId = getCurrentUUID();
		User user = userRepo.findById(currentUserId).get();
		user.setSurveyDetailId(null);
		user.setLatestSpecializedVersion(null);
		userRepo.save(user);
	}

	public UUID getUserIdByUserName(String username) {
		return userRepo.getUserIdByUsername(username);
	}

	public Map<String, Object> getBasicInfo(String userId) {
		Map<String, Object> userInfo = userRepo.getBasicInfo(UUID.fromString(userId));
		if (userInfo.containsKey("birthYear")) {
			Integer birthYear = (Integer) userInfo.get("birthYear");
			Integer age = (birthYear != null) ? java.time.LocalDate.now().getYear() - birthYear : null;
			userInfo.put("age", age);
		}
		return userInfo;
	}

	public void markSurveyDetailId(int surveyId) {
		UUID currentUserId = getCurrentUUID();
		User user = userRepo.findById(currentUserId).get();
		user.setSurveyDetailId(surveyId);
		userRepo.save(user);
	}

	public User getUserById(String userId) {
		return userRepo.findById(UUID.fromString(userId)).get();
	}

	public boolean hasRoleDoctor(User user) {
		return user.getRoles().stream().anyMatch(role -> "ROLE_DOCTOR".equals(role.getName()));
	}

	// Service đặt lại mật khẩu
	public boolean resetPassword(UUID userId, String currentPassword, String newPassword) {
		Optional<User> optionalUser = userRepo.findById(userId);

		if (optionalUser.isPresent()) {
			User user = optionalUser.get();

			// Kiểm tra mật khẩu hiện tại
			if (passwordEncoder.matches(currentPassword, user.getPassword())) {

				// Mã hóa và đặt mật khẩu mới
				user.setPassword(passwordEncoder.encode(newPassword));
				userRepo.save(user);
				return true;
			} else {
				// Nếu mật khẩu hiện tại không đúng
				throw new IllegalArgumentException("Current password is incorrect");
			}
		} else {
			throw new IllegalArgumentException("No user found with the provided ID");
		}
	}

	public Integer getSurveyDetail() {
		UUID currentUserId = getCurrentUUID();
		User user = userRepo.findById(currentUserId).get();
		return user.getSurveyDetailId();
	}
}
