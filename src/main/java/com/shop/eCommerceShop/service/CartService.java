package com.shop.eCommerceShop.service;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Cart;
import com.shop.eCommerceShop.model.User;
import com.shop.eCommerceShop.request.AddItemRequest;

public interface CartService {
	
public Cart createCart(User user);
	
	public String addCartItem(int userId, AddItemRequest req)throws HandleException;
	
	public Cart findUserCart(int userId);
}
