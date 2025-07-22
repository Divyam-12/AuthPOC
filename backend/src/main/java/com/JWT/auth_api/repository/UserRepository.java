package com.JWT.auth_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.JWT.auth_api.model.User;

public interface UserRepository extends JpaRepository<User,Integer>{
	
	Optional<User> findByUsername(String username);
}
