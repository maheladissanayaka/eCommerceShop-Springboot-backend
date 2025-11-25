package com.shop.eCommerceShop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shop.eCommerceShop.model.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
	
	@Query(value = "SELECT DISTINCT o FROM Order o " +
		   "LEFT JOIN FETCH o.orderItems oi " +
		   "LEFT JOIN FETCH oi.product p " +
		   "LEFT JOIN FETCH o.shippingAddress " +
		   "WHERE o.user.id = :userId ORDER BY o.orderDate DESC",
		   countQuery = "SELECT COUNT(DISTINCT o) FROM Order o WHERE o.user.id = :userId")
	List<Order> findByUserIdOrderByOrderDateDesc(@Param("userId") int userId);
	
	@Query("SELECT COUNT(o) FROM Order o WHERE o.shippingAddress.id = :addressId")
	long countByShippingAddressId(@Param("addressId") int addressId);
	
	@Query("SELECT o FROM Order o WHERE o.shippingAddress.id = :addressId")
	List<Order> findByShippingAddressId(@Param("addressId") int addressId);
	
}

