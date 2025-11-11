package com.shop.eCommerceShop.service;

import org.springframework.stereotype.Service;

import com.shop.eCommerceShop.config.JwtProvider;
import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.User;
import com.shop.eCommerceShop.repository.UserRepository;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private JwtProvider jwtProvider;

    public UserServiceImplementation(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public User findUserById(int userId) throws HandleException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new HandleException("User not found with ID: " + userId));
    }

    @Override
    public User findUserProfileByJwt(String jwt) throws HandleException {
    	String email =jwtProvider.getEmailFromToken(jwt);
    	
    	User user = userRepository.findByEmail(email);
    	if(user==null) {
        throw new HandleException("User not found with email"+email);
    	}
    	return user;
    }
}
