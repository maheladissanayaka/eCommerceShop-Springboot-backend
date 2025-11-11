package com.shop.eCommerceShop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shop.eCommerceShop.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer   >{

	@Query("SELECT r FROM Review r WHERE r.product.id = :productId")
	public List<Review> getAllProductsReview(@Param("productId") Integer productId);

}
