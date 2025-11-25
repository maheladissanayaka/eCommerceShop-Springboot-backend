package com.shop.eCommerceShop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Order;
import com.shop.eCommerceShop.responce.ApiResponse;
import com.shop.eCommerceShop.service.OrderService;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping
	public ResponseEntity<List<Order>>getAllOrderHandler(){
		List<Order>orders=orderService.getAOrders();
		return new ResponseEntity<List<Order>>(orders,HttpStatus.OK);
	}
	
	@PutMapping("/{orderId}/confirmed")
	public ResponseEntity<Order> ConfirmedOrderHandler(@PathVariable Integer orderId,
			@RequestHeader("Authorization")String jwt)throws HandleException{
		
		Order order = orderService.confirmedOrder(orderId);
		
		return new ResponseEntity<>(order,HttpStatus.OK);
		
	}
	
	@PutMapping("/{orderId}/ship")
	public ResponseEntity<Order> ShippedOrderHandler(@PathVariable Integer orderId,
			@RequestHeader("Authorization")String jwt)throws HandleException{
		
		Order order = orderService.shippedOrder(orderId);
		
		return new ResponseEntity<>(order,HttpStatus.OK);
	}
	
	@PutMapping("/{orderId}/deliver")
	public ResponseEntity<Order>DeliverOrderHandler(@PathVariable Integer orderId,
			@RequestHeader("Authorization")String jwt)throws HandleException{
		
		Order order = orderService.deliveredOrder(orderId);
		
		return new ResponseEntity<>(order,HttpStatus.OK);
	}
	
	@PutMapping("/{orderId}/cancel")
	public ResponseEntity<Order>CancelOrderHandler(@PathVariable Integer orderId,
			@RequestHeader("Authorization")String jwt)throws HandleException{
		
		Order order = orderService.canceledOrder(orderId);
		
		return new ResponseEntity<>(order,HttpStatus.OK);
	}
	
	@DeleteMapping("/{orderId}/delete")
	public ResponseEntity<ApiResponse> DeleteOrderHandler(@PathVariable Integer orderId,
	        @RequestHeader("Authorization") String jwt) throws HandleException {

	    orderService.deleteOrder(orderId);

	    ApiResponse res = new ApiResponse();
	    res.setMessage("order deleted successfully");
	    res.setStatus(true);

	    return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	
}
