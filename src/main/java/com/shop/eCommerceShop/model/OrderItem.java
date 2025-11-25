package com.shop.eCommerceShop.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class OrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	private Product product;
	
	@ManyToOne
	@JsonIgnore
    private Order order;
	
	private String size;
	
	private int quantity;
	
	private double price;
	
	private double discountPrice;
	
	private int userId;
	
	private LocalDateTime deliveryDate;
	
	public OrderItem() {
		// TODO Auto-generated constructor stub
	}
	
	 

	public OrderItem(int id, Product product, String size, int quantity, double price, double discountPrice, int userId,
			LocalDateTime deliveryDate) {
		super();
		this.id = id;
		this.product = product;
		this.size = size;
		this.quantity = quantity;
		this.price = price;
		this.discountPrice = discountPrice;
		this.userId = userId;
		this.deliveryDate = deliveryDate;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Order getOrder() { return order; } // ✅ Getter
    public void setOrder(Order order) { this.order = order; }

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public LocalDateTime getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(LocalDateTime deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	
	
	
}
