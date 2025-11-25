package com.shop.eCommerceShop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.shop.eCommerceShop.config.JwtProvider;
import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Address;
import com.shop.eCommerceShop.model.Order;
import com.shop.eCommerceShop.model.User;
import com.shop.eCommerceShop.repository.AddressRepository;
import com.shop.eCommerceShop.repository.OrderRepository;
import com.shop.eCommerceShop.repository.UserRepository;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private JwtProvider jwtProvider;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private OrderRepository orderRepository;

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
    
    @Override
    public Address addAddress(String jwt, Address address) throws HandleException {
    	User user = findUserProfileByJwt(jwt);
    	address.setUser(user);
    	Address savedAddress = addressRepository.save(address);
    	user.getAddress().add(savedAddress);
    	userRepository.save(user);
    	return savedAddress;
    }
    
    @Override
    public Address updateAddress(String jwt, int addressId, Address address) throws HandleException {
    	User user = findUserProfileByJwt(jwt);
    	Address existingAddress = addressRepository.findById(addressId)
    			.orElseThrow(() -> new HandleException("Address not found"));
    	
    	// Verify address belongs to user
    	if(existingAddress.getUser().getId() != user.getId()) {
    		throw new HandleException("Address does not belong to user");
    	}
    	
    	existingAddress.setFirstName(address.getFirstName());
    	existingAddress.setLastName(address.getLastName());
    	existingAddress.setStreetAddress(address.getStreetAddress());
    	existingAddress.setCity(address.getCity());
    	existingAddress.setState(address.getState());
    	existingAddress.setZipCode(address.getZipCode());
    	existingAddress.setMobile(address.getMobile());
    	
    	return addressRepository.save(existingAddress);
    }
    
    @Override
    @Transactional
    public void deleteAddress(String jwt, int addressId) throws HandleException {
    	User user = findUserProfileByJwt(jwt);
    	Address address = addressRepository.findById(addressId)
    			.orElseThrow(() -> new HandleException("Address not found"));
    	
    	// Verify address belongs to user
    	if(address.getUser().getId() != user.getId()) {
    		throw new HandleException("Address does not belong to user");
    	}
    	
    	// If address is used in orders, set shipping address to null in those orders
    	List<Order> ordersUsingAddress = orderRepository.findByShippingAddressId(addressId);
    	if (!ordersUsingAddress.isEmpty()) {
    		for (Order order : ordersUsingAddress) {
    			order.setShippingAddress(null);
    			orderRepository.save(order);
    		}
    	}
    	
    	user.getAddress().remove(address);
    	userRepository.save(user);
    	addressRepository.delete(address);
    }
}
