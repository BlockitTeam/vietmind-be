package com.vm.service;

import com.vm.constant.Provider;
import com.vm.repo.UserRepository;
import com.vm.model.User;
import com.vm.request.UserRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
}
