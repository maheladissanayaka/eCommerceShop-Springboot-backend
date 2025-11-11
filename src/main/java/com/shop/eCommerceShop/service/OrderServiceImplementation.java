package com.shop.eCommerceShop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Address;
import com.shop.eCommerceShop.model.Order;
import com.shop.eCommerceShop.model.User;
import com.shop.eCommerceShop.repository.CartRepository;

@Service
public class OrderServiceImplementation implements OrderService{
	
	private CartRepository cartRepository;
	private CartService cartService;
	private ProductService productService;
	
	public OrderServiceImplementation(CartRepository cartRepository,
			CartService cartItemService,
			ProductService productService) {
		this.cartRepository=cartRepository;
		this.cartService=cartService;
		this.productService=productService;
	}
	
	@Override
	public Order createOrder(User user, Address shippingAddress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order findOrderById(int orderId) throws HandleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> userOrderHistory(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order placedOrder(int orderId) throws HandleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order confirmedOrder(int orderId) throws HandleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order shippedOrder(int orderId) throws HandleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order deliveredOrder(int orderId) throws HandleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order canceledOrder(int orderId) throws HandleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getAOrders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteOrder(int orderId) throws HandleException {
		// TODO Auto-generated method stub
		
	}
	

}
