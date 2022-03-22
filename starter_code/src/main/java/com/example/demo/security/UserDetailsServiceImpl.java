package com.example.demo.security;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private static final String INVALID_USER_ERROR_MESSAGE = "Attempt to load invalid user: ";

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
        	// The following code complies with the method contract
        	//
            // throw new UsernameNotFoundException(username);
        	//
        	// However, it also causes a stack overflow.
        	// I believe this is a bug in the Spring framework
        	// For now, I will return a 'null' to avoid the stack overflow condition
        	// and log a bug against the Spring Framework.
        	logger.error(INVALID_USER_ERROR_MESSAGE + username);
        	return null;
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }
}