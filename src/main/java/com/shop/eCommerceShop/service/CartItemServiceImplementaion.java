package com.shop.eCommerceShop.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Cart;
import com.shop.eCommerceShop.model.CartItem;
import com.shop.eCommerceShop.model.Product;
import com.shop.eCommerceShop.model.User;
import com.shop.eCommerceShop.repository.CartItemRepository;
import com.shop.eCommerceShop.repository.CartRepository;

@Service
public class CartItemServiceImplementaion implements CartItemService{

	private CartItemRepository cartItemRepository;
	private UserService userService;
	private CartRepository cartRepository;
	
	public CartItemServiceImplementaion(CartItemRepository cartItemRepository,
			UserService userService,CartRepository cartRepository) {
		this.cartItemRepository=cartItemRepository;
		this.userService=userService;
		this.cartRepository=cartRepository;
	}
	
	@Override
	public CartItem createCartItem(CartItem cartItem) {
		cartItem.setQuantity(1);
		cartItem.setPrice(cartItem.getProduct().getPrice()*cartItem.getQuantity());
		cartItem.setDiscountedPrice(cartItem.getProduct().getDiscountPrice()*cartItem.getQuantity());
		
		CartItem createdCartItem=cartItemRepository.save(cartItem);
		return createdCartItem;
	}

	@Override
	public CartItem updateCartItem(Integer userId, Integer id, CartItem cartItem) throws HandleException {

		CartItem item=findCartItemById(id);
		User user = userService.findUserById(item.getUserId());
		
		if(user.getId()== userId) {
			item.setQuantity(cartItem.getQuantity());
			item.setPrice(item.getQuantity()*item.getProduct().getPrice());
			item.setDiscountedPrice(item.getProduct().getDiscountPrice()*item.getQuantity());
		}
		return cartItemRepository.save(item);
	}

	@Override
	public CartItem isCartItemExist(Cart cart, Product product, String size, Integer userId) {

		CartItem cartItem = cartItemRepository.isCartItemExist(cart, product, size, userId);
		
		return cartItem;
	}

	@Override
	public void removeCartItem(Integer userId, Integer cartItemId) throws HandleException {
		CartItem cartItem = findCartItemById(cartItemId);
		
		User user = userService.findUserById(cartItem.getUserId());
		
		User reqUser=userService.findUserById(userId);
		
		if(user.getId()== reqUser.getId()) {
			cartItemRepository.deleteById(cartItemId);
		}else {
			throw new HandleException("You can't remove another user item");
		}
		
	}

	@Override
	public CartItem findCartItemById(Integer cartItemId) throws HandleException {
		Optional<CartItem> opt =cartItemRepository.findById(cartItemId);
		
		if(opt.isPresent()) {
			return opt.get();
		}
		throw new HandleException("cart Item not found with id: "+cartItemId);
	}

}
