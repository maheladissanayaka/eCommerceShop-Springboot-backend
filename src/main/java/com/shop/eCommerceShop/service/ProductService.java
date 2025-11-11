package com.shop.eCommerceShop.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Product;
import com.shop.eCommerceShop.request.CreateProductRequest;

public interface ProductService {
	
	public Product createProduct(CreateProductRequest req);
	
	public String deleteProduct(int productId)throws HandleException;
	
	public Product updateProduct(int productId, Product req)throws HandleException;
	
	public Product findProductById(int id)throws HandleException;
	
	public List<Product> findAllProduct() throws HandleException;
	
	public List<Product>findProductByCategory(String category);
	
	public Page<Product>getAllProduct(String category, List<String>colors, List<String>sizes, Integer minPrice, Integer maxPrice,
			Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize);
}
 