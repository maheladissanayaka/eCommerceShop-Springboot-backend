package com.shop.eCommerceShop.model;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class Size {
	
//	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
	
	private String name;
	private int quantity;
	
	public Size() {
		// TODO Auto-generated constructor stub
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	

}
