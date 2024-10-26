package com.eac.store.repository;

import org.springframework.data.repository.CrudRepository;

import com.eac.store.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
	User findByEmail(String email);
}