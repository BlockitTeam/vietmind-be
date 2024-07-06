package com.vm.service;

import com.vm.constant.Provider;
import com.vm.dto.UserDTO;
import com.vm.model.Role;
import com.vm.model.User;
import com.vm.repo.RoleRepository;
import com.vm.repo.UserRepository;
import com.vm.request.UserRequest;
import com.vm.service.impl.CustomOAuth2User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private ModelMapper modelMapper;

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
		User existUser = userRepo.getUserByUsername(username);
		if (existUser != null)
			existUser.setPassword(null);
		return existUser;
	}

	public List<User> getDoctors() {
		return userRepo.getDoctors();
	}

	public List<UserDTO> getDoctorsWithConversations(UUID userId) {
		List<Object[]> results = userRepo.getDoctorsWithConversationsByUserId(userId);

		return results.stream()
				.map(result -> {
					User user = (User) result[0];
					Integer conversationId = (Integer) result[1];
					UserDTO userDTO = modelMapper.map(user, UserDTO.class);
					userDTO.setConversationId(conversationId);
					return userDTO;
				})
				.collect(Collectors.toList());
	}

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
}
