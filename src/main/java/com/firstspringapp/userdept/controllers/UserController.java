package com.firstspringapp.userdept.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.firstspringapp.userdept.entities.User;
import com.firstspringapp.userdept.repositories.UserRepository;

@RestController
@RequestMapping(value = "/users")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User already exist")
	@ExceptionHandler(DataIntegrityViolationException.class)
	private void conflict() {
	}

	@GetMapping
	public List<User> findAll() {
		List<User> result = userRepository.findAll();
		return result;
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<?> findById(@PathVariable Long id) {
		Optional<User> user = userRepository.findById(id);
		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
		
		User result = user.get();
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	@PostMapping
	public ResponseEntity<?> insert(@RequestBody User user) {
		Optional<User> alreadyExist = userRepository.findByEmail(user.getEmail());
		if (!alreadyExist.isEmpty()) {
			ResponseEntity<String> response =  ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
			return response;
		}
		
		User result = userRepository.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User user) {
		Optional<User> alreadyExist = userRepository.findById(id);
		if (alreadyExist.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
		
		User body = new User(id, user.getName(), user.getEmail(), user.getDepartment());
		User result = userRepository.save(body);
		return ResponseEntity.ok(result);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Optional<User> alreadyExist = userRepository.findById(id);
		if (alreadyExist.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
		userRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
