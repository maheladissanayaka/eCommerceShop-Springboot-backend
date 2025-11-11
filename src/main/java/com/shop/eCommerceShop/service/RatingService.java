package com.shop.eCommerceShop.service;

import java.util.List;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Rating;
import com.shop.eCommerceShop.model.User;
import com.shop.eCommerceShop.request.RatingRequest;

public interface RatingService {
	
	public Rating createRating(RatingRequest req,User user)throws HandleException;
	
	public List<Rating> getProductsRating(Integer productId);
}
