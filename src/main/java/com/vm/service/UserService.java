package com.vm.service;

import com.vm.constant.Provider;
import com.vm.repo.UserRepository;
import com.vm.model.User;
import com.vm.request.UserRequest;
import com.vm.service.impl.CustomOAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private UserRepository repo;

	public void processOAuthPostLogin(String username, Provider provider) {
		User existUser = repo.getUserByUsername(username);
		if (existUser == null) {
			User newUser = new User();
			newUser.setUsername(username);
			newUser.setProvider(Provider.GOOGLE);
			newUser.setEnabled(true);			
			
			repo.save(newUser);
			
			System.out.println("Created new user: " + username);
		}
	}

	public User getCurrentUser(String username) {
		User existUser = repo.getUserByUsername(username);
		return existUser;
	}

	public User update(UserRequest request, String userName) {
		User originUser = repo.getUserByUsername(userName);
		originUser.setFirstName(request.getFirstName());
		originUser.setLastName(request.getLastName());
		originUser.setBirthYear(request.getBirthYear());
		originUser.setGender(request.getGender());
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

	public Long getCurrentUserId() {
		String userName = getCurrentUserName();
		return repo.getUserIdByUsername(userName);
	}
}
