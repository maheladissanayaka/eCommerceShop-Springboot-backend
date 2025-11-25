package com.shop.eCommerceShop.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Address;
import com.shop.eCommerceShop.model.Cart;
import com.shop.eCommerceShop.model.CartItem;
import com.shop.eCommerceShop.model.Order;
import com.shop.eCommerceShop.model.OrderItem;
import com.shop.eCommerceShop.model.Product;
import com.shop.eCommerceShop.model.User;
import com.shop.eCommerceShop.repository.AddressRepository;
import com.shop.eCommerceShop.repository.CartItemRepository;
import com.shop.eCommerceShop.repository.CartRepository;
import com.shop.eCommerceShop.repository.OrderRepository;

@Service
public class OrderServiceImplementation implements OrderService{
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private com.shop.eCommerceShop.repository.productRepository productRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	@Override
	public Order createOrder(User user, Address shippingAddress) throws HandleException {
		// Get user's cart - use cartService to ensure cart items are loaded
		Cart cart = cartService.findUserCart(user.getId());
		
		if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
			throw new HandleException("Cart is empty");
		}
		
		// Use existing address if ID is provided, otherwise create a new one
		Address savedAddress;
		if (shippingAddress.getId() > 0) {
			// Use existing address - verify it belongs to the user
			savedAddress = addressRepository.findById(shippingAddress.getId())
				.orElseThrow(() -> new HandleException("Address not found"));
			if (savedAddress.getUser().getId() != user.getId()) {
				throw new HandleException("Address does not belong to user");
			}
		} else {
			// Create new address
			savedAddress = new Address();
			savedAddress.setFirstName(shippingAddress.getFirstName());
			savedAddress.setLastName(shippingAddress.getLastName());
			savedAddress.setStreetAddress(shippingAddress.getStreetAddress());
			savedAddress.setCity(shippingAddress.getCity());
			savedAddress.setState(shippingAddress.getState());
			savedAddress.setZipCode(shippingAddress.getZipCode());
			savedAddress.setMobile(shippingAddress.getMobile());
			savedAddress.setUser(user);
			savedAddress = addressRepository.save(savedAddress);
		}
		
		// Create order
		Order order = new Order();
		order.setUser(user);
		order.setOrderId(UUID.randomUUID().toString());
		order.setOrderDate(LocalDateTime.now());
		order.setOrderStatus("PENDING");
		order.setShippingAddress(savedAddress);
		order.setCreateAt(LocalDateTime.now());
		
		// Convert cart items to order items
		List<OrderItem> orderItems = new ArrayList<>();
		double totalPrice = 0;
		double totalDiscountedPrice = 0;
		int totalItem = 0;
		
		for (CartItem cartItem : cart.getCartItems()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setSize(cartItem.getSize());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setPrice(cartItem.getPrice());
			orderItem.setDiscountPrice(cartItem.getDiscountedPrice());
			orderItem.setUserId(user.getId());
			
			orderItems.add(orderItem);
			totalPrice += cartItem.getPrice();
			totalDiscountedPrice += cartItem.getDiscountedPrice();
			totalItem += cartItem.getQuantity();
			
			// Update stock quantity for the specific size
			// Reload product from database to ensure sizes are loaded
			Product product = productService.findProductById(cartItem.getProduct().getId());
			if (product != null && product.getSizes() != null && cartItem.getSize() != null) {
				// Find the size in the product's sizes set
				boolean sizeFound = false;
				for (com.shop.eCommerceShop.model.Size size : product.getSizes()) {
					if (size.getName() != null && size.getName().equals(cartItem.getSize())) {
						// Decrement the quantity
						int currentQuantity = size.getQuantity();
						int orderedQuantity = cartItem.getQuantity();
						
						// Validate stock availability
						if (currentQuantity < orderedQuantity) {
							throw new HandleException("Insufficient stock for size " + cartItem.getSize() + 
								" of product " + product.getTitle() + ". Available: " + currentQuantity + ", Requested: " + orderedQuantity);
						}
						
						int newQuantity = currentQuantity - orderedQuantity;
						size.setQuantity(newQuantity);
						sizeFound = true;
						break;
					}
				}
				
				if (!sizeFound) {
					throw new HandleException("Size " + cartItem.getSize() + " not found for product " + product.getTitle());
				}
				
				// Save the updated product with modified sizes
				productRepository.save(product);
			}
		}
		
		order.setOrderItems(orderItems);
		order.setTotalPrice(totalPrice);
		order.setTotalDiscountPrice(totalDiscountedPrice);
		order.setDiscount(totalPrice - totalDiscountedPrice);
		order.setTotalItem(totalItem);
		
		// Use persist to ensure ID is generated by database
		entityManager.persist(order);
		entityManager.flush();
		entityManager.refresh(order);
		
		Order savedOrder = order;
		
		// Clear cart items after order is created
		cart.getCartItems().clear();
		cart.setTotalPrice(0.0);
		cart.setTotalDiscountedPrice(0.0);
		cart.setTotalItem(0);
		cart.setDicounte(0.0);
		cartRepository.save(cart);
		
		return savedOrder;
	}

	@Override
	@Transactional(readOnly = true)
	public Order findOrderById(int orderId) throws HandleException {
		String jpql = "SELECT DISTINCT o FROM Order o " +
		              "LEFT JOIN FETCH o.orderItems oi " +
		              "LEFT JOIN FETCH oi.product p " +
		              "LEFT JOIN FETCH o.shippingAddress sa " +
		              "LEFT JOIN FETCH o.user u " +
		              "WHERE o.id = :orderId";
		
		TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);
		query.setParameter("orderId", orderId);
		
		try {
			Order order = query.getSingleResult();
			// Force initialization of collections
			if (order.getOrderItems() != null) {
				order.getOrderItems().size();
				for (OrderItem item : order.getOrderItems()) {
					if (item.getProduct() != null) {
						item.getProduct().getTitle();
					}
				}
			}
			if (order.getShippingAddress() != null) {
				order.getShippingAddress().getStreetAddress();
			}
			return order;
		} catch (Exception e) {
			throw new HandleException("Order not found with id: " + orderId);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Order> userOrderHistory(int userId) {
		// First, get all order IDs for the user
		String orderIdsJpql = "SELECT o.id FROM Order o WHERE o.user.id = :userId ORDER BY o.orderDate DESC";
		TypedQuery<Integer> orderIdsQuery = entityManager.createQuery(orderIdsJpql, Integer.class);
		orderIdsQuery.setParameter("userId", userId);
		List<Integer> orderIds = orderIdsQuery.getResultList();
		
		if (orderIds.isEmpty()) {
			return new ArrayList<>();
		}
		
		// Then fetch each order with all its relationships individually
		// This ensures all orderItems are loaded correctly
		List<Order> orders = new ArrayList<>();
		for (Integer orderId : orderIds) {
			// Use a more explicit query that selects all fields
			String jpql = "SELECT DISTINCT o FROM Order o " +
			              "LEFT JOIN FETCH o.orderItems oi " +
			              "LEFT JOIN FETCH oi.product p " +
			              "LEFT JOIN FETCH o.shippingAddress sa " +
			              "LEFT JOIN FETCH o.user u " +
			              "WHERE o.id = :orderId";
			
			TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);
			query.setParameter("orderId", orderId);
			try {
				Order order = query.getSingleResult();
				
				// Force initialization of orderItems collection
				if (order.getOrderItems() != null) {
					// Access the collection to force initialization
					for (OrderItem item : order.getOrderItems()) {
						if (item.getProduct() != null) {
							// Access product fields to force loading
							item.getProduct().getTitle();
						}
					}
				}
				
				// Force initialization of shipping address
				if (order.getShippingAddress() != null) {
					order.getShippingAddress().getStreetAddress();
				}
				
				orders.add(order);
			} catch (Exception e) {
				// Log error but don't throw - continue with other orders
			}
		}
		
		return orders;
	}

	@Override
	public Order placedOrder(int orderId) throws HandleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public Order confirmedOrder(int orderId) throws HandleException {
		Order order = findOrderById(orderId);
		if (order.getOrderStatus().equals("DELIVERED")) {
			throw new HandleException("Cannot change status of a delivered order");
		}
		if (order.getOrderStatus().equals("CANCELLED")) {
			throw new HandleException("Cannot change status of a cancelled order");
		}
		order.setOrderStatus("CONFIRMED");
		return orderRepository.save(order);
	}

	@Override
	@Transactional
	public Order shippedOrder(int orderId) throws HandleException {
		Order order = findOrderById(orderId);
		if (order.getOrderStatus().equals("DELIVERED")) {
			throw new HandleException("Cannot change status of a delivered order");
		}
		if (order.getOrderStatus().equals("CANCELLED")) {
			throw new HandleException("Cannot change status of a cancelled order");
		}
		order.setOrderStatus("SHIPPED");
		return orderRepository.save(order);
	}

	@Override
	@Transactional
	public Order deliveredOrder(int orderId) throws HandleException {
		Order order = findOrderById(orderId);
		if (!order.getOrderStatus().equals("SHIPPED")) {
			throw new HandleException("Order must be shipped before it can be delivered");
		}
		order.setOrderStatus("DELIVERED");
		order.setDeliveryDate(LocalDateTime.now());
		return orderRepository.save(order);
	}

	@Override
	@Transactional
	public Order canceledOrder(int orderId) throws HandleException {
		Order order = findOrderById(orderId);
		if (order.getOrderStatus().equals("DELIVERED")) {
			throw new HandleException("Cannot cancel a delivered order");
		}
		if (order.getOrderStatus().equals("CANCELLED")) {
			throw new HandleException("Order is already cancelled");
		}
		
		// Restore stock quantities when cancelling
		if (order.getOrderItems() != null) {
			for (OrderItem item : order.getOrderItems()) {
				Product product = productService.findProductById(item.getProduct().getId());
				if (product != null && product.getSizes() != null && item.getSize() != null) {
					for (com.shop.eCommerceShop.model.Size size : product.getSizes()) {
						if (size.getName() != null && size.getName().equals(item.getSize())) {
							int currentQuantity = size.getQuantity();
							int canceledQuantity = item.getQuantity();
							size.setQuantity(currentQuantity + canceledQuantity);
							productRepository.save(product);
							break;
						}
					}
				}
			}
		}
		
		order.setOrderStatus("CANCELLED");
		return orderRepository.save(order);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Order> getAOrders() {
		// Get all order IDs
		String orderIdsJpql = "SELECT o.id FROM Order o ORDER BY o.orderDate DESC";
		TypedQuery<Integer> orderIdsQuery = entityManager.createQuery(orderIdsJpql, Integer.class);
		List<Integer> orderIds = orderIdsQuery.getResultList();
		
		if (orderIds.isEmpty()) {
			return new ArrayList<>();
		}
		
		// Fetch each order with all relationships
		List<Order> orders = new ArrayList<>();
		for (Integer orderId : orderIds) {
			String jpql = "SELECT DISTINCT o FROM Order o " +
			              "LEFT JOIN FETCH o.orderItems oi " +
			              "LEFT JOIN FETCH oi.product p " +
			              "LEFT JOIN FETCH o.shippingAddress sa " +
			              "LEFT JOIN FETCH o.user u " +
			              "WHERE o.id = :orderId";
			
			TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);
			query.setParameter("orderId", orderId);
			try {
				Order order = query.getSingleResult();
				// Force initialization of collections
				if (order.getOrderItems() != null) {
					order.getOrderItems().size();
					for (OrderItem item : order.getOrderItems()) {
						if (item.getProduct() != null) {
							item.getProduct().getTitle();
						}
					}
				}
				if (order.getShippingAddress() != null) {
					order.getShippingAddress().getStreetAddress();
				}
				orders.add(order);
			} catch (Exception e) {
				// Log error but continue with other orders
				System.err.println("Error loading order " + orderId + ": " + e.getMessage());
			}
		}
		return orders;
	}

	@Override
	@Transactional
	public void deleteOrder(int orderId) throws HandleException {
		Order order = findOrderById(orderId);
		
		// Restore stock quantities when deleting
		if (order.getOrderItems() != null) {
			for (OrderItem item : order.getOrderItems()) {
				Product product = productService.findProductById(item.getProduct().getId());
				if (product != null && product.getSizes() != null && item.getSize() != null) {
					for (com.shop.eCommerceShop.model.Size size : product.getSizes()) {
						if (size.getName() != null && size.getName().equals(item.getSize())) {
							int currentQuantity = size.getQuantity();
							int deletedQuantity = item.getQuantity();
							size.setQuantity(currentQuantity + deletedQuantity);
							productRepository.save(product);
							break;
						}
					}
				}
			}
		}
		
		orderRepository.delete(order);
	}
	

}
