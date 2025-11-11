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
	
	public CartServiceImplementation(CartRepository cartRepository,
			CartItemService cartItemService,ProductService productService) {
		this.cartRepository=cartRepository;
		this.cartItemService=cartItemService;
		this.productService=productService;
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
		Product product = productService.findProductById(req.getProductId());
		
		CartItem isPresent = cartItemService.isCartItemExist(cart, product, req.getSize(), userId);
		
		if(isPresent==null) {
			CartItem cartItem = new CartItem();
			cartItem.setProduct(product);
			cartItem.setCart(cart);
			cartItem.setQuantity(req.getQuantity());
			cartItem.setUserId(userId);
			
			double price = req.getQuantity()*product.getDiscountPrice();
			cartItem.setPrice(price);
			cartItem.setSize(req.getSize());
			
			CartItem createdCartItem=cartItemService.createCartItem(cartItem);
			cart.getCartItems().add(createdCartItem);
		}
		return "Item Add To Cart";
	}

	@Override
	public Cart findUserCart(int userId) {
		Cart cart = cartRepository.findByUserId(userId);
		
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
