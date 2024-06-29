package com.vm.service;

import com.vm.constant.Provider;
import com.vm.dto.UserDTO;
import com.vm.repo.UserRepository;
import com.vm.model.User;
import com.vm.request.UserRequest;
import com.vm.service.impl.CustomOAuth2User;
import com.vm.util.EncryptionUtil;
import com.vm.util.KeyManagement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.*;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
	@Autowired
	private UserRepository repo;

	@Autowired
	private ModelMapper modelMapper;

	public void processOAuthPostLogin(String username, Provider provider) throws Exception {
		User existUser = repo.getUserByUsername(username);
		if (existUser == null) {
			User newUser = new User();
			newUser.setUsername(username);
			newUser.setProvider(provider);
			repo.save(newUser);
			System.out.println("Created new user: " + username);
		}
	}

	public User getCurrentUser(String username) {
		User existUser = repo.getUserByUsername(username);
		existUser.setPassword(null);
		return existUser;
	}

	public List<User> getDoctors() {
		return repo.getDoctors();
	}

	public List<UserDTO> getDoctorsWithConversations(UUID userId) {
		List<Object[]> results = repo.getDoctorsWithConversationsByUserId(userId);

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
		User originUser = repo.getUserByUsername(userName);
		originUser.setFirstName(request.getFirstName());
		originUser.setLastName(request.getLastName());
		originUser.setBirthYear(request.getBirthYear());
		originUser.setGender(request.getGender());

		//Enable user after finish complete form register
		originUser.setEnabled(true);
		return repo.save(originUser);
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

	public UUID getCurrentUserId() {
		String userName = getCurrentUserName();
		return repo.getUserIdByUsername(userName);
	}

	public void markCompleteGeneralSurvey(boolean surveyCompleted) {
		UUID currentUserId = getCurrentUserId();
		User user = repo.findById(currentUserId).get();
		user.setSurveyCompleted(surveyCompleted);
		repo.save(user);
	}

	public UUID getUserIdByUserName(String username) {
		return repo.getUserIdByUsername(username);
	}

	public String getPublicKeyByUserId (String user_id) {
		return repo.getPublicKeyByUserid(UUID.fromString(user_id));
	}
}
