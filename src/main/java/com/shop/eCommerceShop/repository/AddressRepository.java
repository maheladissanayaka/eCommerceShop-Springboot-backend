package com.shop.eCommerceShop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.eCommerceShop.model.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {
	
}

