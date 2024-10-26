package com.eac.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eac.store.model.User;
import com.eac.store.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
 	
 	public void save(User user) {
 		userRepository.save(user);
 	}
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
}