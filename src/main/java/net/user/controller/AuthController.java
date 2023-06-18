package net.user.controller;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.user.dto.LoginDto;
import net.user.dto.SignUpDto;
import net.user.entity.Role;
import net.user.entity.User;
import net.user.repository.RoleRepository;
import net.user.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200/")
public class AuthController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	
	@Autowired
	private UserRepository userRepository;

	
	@Autowired
	private RoleRepository roleRepository;
	
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping("/signin")
	public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto )
	{
		Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return new ResponseEntity<String>("User singed-in successfully!!", HttpStatus.OK);
	}
	
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto)
	{
		if(userRepository.existsByUsername(signUpDto.getUserName()))
		{
			return new ResponseEntity<>("Username is already taken!!", HttpStatus.BAD_REQUEST);
		}
		
		if(userRepository.existsByEmail(signUpDto.getEmail()))
		{
			return new ResponseEntity<>("Email is already taken!!", HttpStatus.BAD_REQUEST);
		}
		
		User user=new User();
		user.setEmail(signUpDto.getEmail());
		user.setName(signUpDto.getName());
		user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
		user.setUsername(signUpDto.getUserName());
		
		
		Role roles=roleRepository.findByName("ROLE_ADMIN").get();
		user.setRoles(Collections.singleton(roles));
		
		
		userRepository.save(user);
		
		return new ResponseEntity<>("User Registered Successfully",HttpStatus.CREATED);
		
	}
}
