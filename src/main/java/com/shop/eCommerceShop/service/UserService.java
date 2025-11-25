package com.shop.eCommerceShop.service;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Address;
import com.shop.eCommerceShop.model.User;

public interface UserService {
	
	public User findUserById(int userId)throws HandleException;
	
	public User findUserProfileByJwt(String jwt)throws HandleException;
	
	public Address addAddress(String jwt, Address address) throws HandleException;
	
	public Address updateAddress(String jwt, int addressId, Address address) throws HandleException;
	
	public void deleteAddress(String jwt, int addressId) throws HandleException;

}
