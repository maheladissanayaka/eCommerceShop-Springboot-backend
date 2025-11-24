package com.shop.eCommerceShop.service;

import org.springframework.stereotype.Service;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Cart;
import com.shop.eCommerceShop.model.CartItem;
import com.shop.eCommerceShop.model.Product;
import com.shop.eCommerceShop.model.User;
import com.shop.eCommerceShop.repository.CartRepository;
import com.shop.eCommerceShop.request.AddItemRequest;

@Service
public class CartServiceImplementation implements CartService {
	
	private CartRepository cartRepository;
	private CartItemService cartItemService;
	private ProductService productService;
	private UserService userService;
	
	public CartServiceImplementation(CartRepository cartRepository,
			CartItemService cartItemService,ProductService productService, UserService userService) {
		this.cartRepository=cartRepository;
		this.cartItemService=cartItemService;
		this.productService=productService;
		this.userService=userService;
	}
	
	@Override
	public Cart createCart(User user) {
		Cart cart = new Cart();
		cart.setUser(user);
		return cartRepository.save(cart);
	}

	@Override
	public String addCartItem(int userId, AddItemRequest req) throws HandleException {
		
		Cart cart = cartRepository.findByUserId(userId);
		
		// Create cart if it doesn't exist
		if(cart == null) {
			User user = userService.findUserById(userId);
			cart = createCart(user);
		}
		
		Product product = productService.findProductById(req.getProductId());
		
		CartItem isPresent = cartItemService.isCartItemExist(cart, product, req.getSize(), userId);
		
		if(isPresent==null) {
			CartItem cartItem = new CartItem();
			cartItem.setProduct(product);
			cartItem.setCart(cart);
			cartItem.setQuantity(req.getQuantity());
			cartItem.setUserId(userId);
			cartItem.setSize(req.getSize());
			
			// createCartItem will calculate both price and discountedPrice based on quantity
			CartItem createdCartItem=cartItemService.createCartItem(cartItem);
			cart.getCartItems().add(createdCartItem);
		}
		return "Item Add To Cart";
	}

	@Override
	public Cart findUserCart(int userId) {
		Cart cart = cartRepository.findByUserId(userId);
		
		// Create cart if it doesn't exist
		if(cart == null) {
			try {
				User user = userService.findUserById(userId);
				cart = createCart(user);
			} catch (HandleException e) {
				// If user not found, return empty cart or handle error
				return null;
			}
		}
		
		double totalPrice = 0;
		double totalDiscountedPrice=0;
		int totalItem=0;
		
		for(CartItem cartItem : cart.getCartItems()) {
			totalPrice=totalPrice+cartItem.getPrice();
			totalDiscountedPrice=totalDiscountedPrice+cartItem.getDiscountedPrice();
			totalItem=totalItem+cartItem.getQuantity();
		}
		
		cart.setTotalDiscountedPrice(totalDiscountedPrice);
		cart.setTotalItem(totalItem);
		cart.setTotalPrice(totalPrice);
		cart.setDicounte(totalPrice-totalDiscountedPrice);
		return cartRepository.save(cart);
	}

}
