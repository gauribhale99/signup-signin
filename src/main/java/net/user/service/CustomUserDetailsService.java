package net.user.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import net.user.entity.Role;
import net.user.entity.User;
import net.user.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user=userRepository.findByUsernameOrEmail(username, username)
				.orElseThrow(() ->new UsernameNotFoundException("User not found with username or Email:"+ username));
		
				return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), mapRolesToAuthrities(user.getRoles()));
	}
	
	
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthrities(Set<Role> roles)
	{
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}
 
}
