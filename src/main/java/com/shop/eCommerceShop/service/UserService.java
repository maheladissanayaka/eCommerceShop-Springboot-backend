package com.shop.eCommerceShop.service;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.User;

public interface UserService {
	
	public User findUserById(int userId)throws HandleException;
	
	public User findUserProfileByJwt(String jwt)throws HandleException;

}
