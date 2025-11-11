package com.shop.eCommerceShop.service;

import java.util.List;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Review;
import com.shop.eCommerceShop.model.User;
import com.shop.eCommerceShop.request.ReviewRequest;

public interface ReviewService {
	
	public Review createReview(ReviewRequest req, User user)throws HandleException;
	public List<Review>getAllReview(Integer productId);
}
