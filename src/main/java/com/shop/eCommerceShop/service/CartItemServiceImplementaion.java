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
		// Don't override quantity - use the quantity that was already set
		// If quantity is 0 or less, default to 1
		if(cartItem.getQuantity() <= 0) {
			cartItem.setQuantity(1);
		}
		
		double productPrice = cartItem.getProduct().getPrice();
		double productDiscountPrice = cartItem.getProduct().getDiscountPrice();
		
		// If discountPrice is 0 or less, use regular price (no discount)
		if(productDiscountPrice <= 0) {
			productDiscountPrice = productPrice;
		}
		
		cartItem.setPrice(productPrice * cartItem.getQuantity());
		cartItem.setDiscountedPrice(productDiscountPrice * cartItem.getQuantity());
		
		CartItem createdCartItem=cartItemRepository.save(cartItem);
		return createdCartItem;
	}

	@Override
	public CartItem updateCartItem(Integer userId, Integer id, CartItem cartItem) throws HandleException {

		CartItem item=findCartItemById(id);
		User user = userService.findUserById(item.getUserId());
		
		if(user.getId()== userId) {
			item.setQuantity(cartItem.getQuantity());
			
			double productPrice = item.getProduct().getPrice();
			double productDiscountPrice = item.getProduct().getDiscountPrice();
			
			// If discountPrice is 0 or less, use regular price (no discount)
			if(productDiscountPrice <= 0) {
				productDiscountPrice = productPrice;
			}
			
			item.setPrice(item.getQuantity() * productPrice);
			item.setDiscountedPrice(productDiscountPrice * item.getQuantity());
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
