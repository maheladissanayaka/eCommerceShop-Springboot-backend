package com.shop.eCommerceShop.service;

import java.util.List;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Address;
import com.shop.eCommerceShop.model.Order;
import com.shop.eCommerceShop.model.User;

public interface OrderService {
	
	public Order createOrder(User user, Address shippingAddress) throws HandleException;
	
	public Order findOrderById(int orderId) throws HandleException;
	
	public List<Order>userOrderHistory(int userId);
	
	public Order placedOrder(int orderId) throws HandleException;
	
	public Order confirmedOrder(int orderId) throws HandleException;
	
	public Order shippedOrder(int orderId) throws HandleException;
	
	public Order deliveredOrder(int orderId) throws HandleException;
	
	public Order canceledOrder(int orderId) throws HandleException;
	
	public List<Order>getAOrders();
	
	public void deleteOrder(int orderId) throws HandleException;
	

}
