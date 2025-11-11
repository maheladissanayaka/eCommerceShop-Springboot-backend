package com.shop.eCommerceShop.service;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Cart;
import com.shop.eCommerceShop.model.CartItem;
import com.shop.eCommerceShop.model.Product;

public interface CartItemService {
	
	public CartItem createCartItem(CartItem cartItem);
	
	public CartItem updateCartItem(Integer userId,Integer id, CartItem cartItem)throws HandleException;
	
	public CartItem isCartItemExist(Cart cart,Product product, String size, Integer userId);
	
	public void removeCartItem(Integer userId, Integer cartItemId)throws HandleException;
	
	public CartItem findCartItemById(Integer cartItemId)throws HandleException;
}
