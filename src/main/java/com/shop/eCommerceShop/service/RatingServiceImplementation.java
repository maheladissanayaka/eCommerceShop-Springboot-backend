package com.shop.eCommerceShop.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Product;
import com.shop.eCommerceShop.model.Rating;
import com.shop.eCommerceShop.model.User;
import com.shop.eCommerceShop.repository.RatingRepository;
import com.shop.eCommerceShop.request.RatingRequest;

@Service
public class RatingServiceImplementation implements RatingService{
	
	private RatingRepository ratingRepository;
	private ProductService productService;
	
	public RatingServiceImplementation(RatingRepository ratingRepository,ProductService productService) {
		this.ratingRepository=ratingRepository;
		this.productService=productService;
	}
	
	@Override
	public Rating createRating(RatingRequest req, User user) throws HandleException {
		Product product = productService.findProductById(req.getProductId());
		
		Rating rating = new Rating();
		rating.setProduct(product);
		rating.setUser(user);
		rating.setRating(req.getRating());
		rating.setCreatedAt(LocalDateTime.now());
		
		return ratingRepository.save(rating);
	}

	@Override
	public List<Rating> getProductsRating(Integer productId) {
		return ratingRepository.getAllProductsRating(productId);
	}

}
