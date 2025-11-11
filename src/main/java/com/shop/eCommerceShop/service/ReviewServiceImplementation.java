package com.shop.eCommerceShop.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Product;
import com.shop.eCommerceShop.model.Review;
import com.shop.eCommerceShop.model.User;
import com.shop.eCommerceShop.repository.ReviewRepository;
import com.shop.eCommerceShop.repository.productRepository;
import com.shop.eCommerceShop.request.ReviewRequest;

@Service
public class ReviewServiceImplementation implements ReviewService{
	
	private ReviewRepository reviewRepository;
	private ProductService productService;
	private productRepository productRepository;
	
	public ReviewServiceImplementation(ReviewRepository reviewRepository,
			ProductService productService,productRepository productRepository) {
		this.reviewRepository=reviewRepository;
		this.productService=productService;
		this.productRepository=productRepository;
	}
	
	@Override
	public Review createReview(ReviewRequest req, User user) throws HandleException {
		Product product=productService.findProductById(req.getProductId());
		
		Review review = new Review();
		review.setUser(user);
		review.setProduct(product);
		review.setReview(req.getReview());
		review.setCreatedAt(LocalDateTime.now());
		
		return reviewRepository.save(review);
	}

	@Override
	public List<Review> getAllReview(Integer productId) {
		
		return reviewRepository.getAllProductsReview(productId);
	}
	
	
}
