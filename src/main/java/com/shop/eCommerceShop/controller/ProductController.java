package com.shop.eCommerceShop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Product;
import com.shop.eCommerceShop.service.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@GetMapping("/products")
	public ResponseEntity<Page<Product>>findProductByCategoryHandler(
			@RequestParam(required = false) String category,
			@RequestParam(required = false) List<String> color,
			@RequestParam(required = false) List<String> size,
			@RequestParam(required = false) Integer minPrice,
			@RequestParam(required = false) Integer maxPrice,
			@RequestParam(required = false) Integer minDiscount,
			@RequestParam(required = false, defaultValue = "price_low") String sort,
			@RequestParam(required = false) String stock,
			@RequestParam(required = false, defaultValue = "0") Integer pageNumber,
			@RequestParam(required = false, defaultValue = "12") Integer pageSize){
		
		Page<Product> res = productService.getAllProduct(category, color, size, 
				minPrice, maxPrice, minDiscount, sort, 
				stock, pageNumber, pageSize);
		
		System.out.println("complete products");
		return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/product/id/{productId}")
	public ResponseEntity<Product>findProductByIdHandler(@PathVariable Integer productId)throws HandleException{
		
		Product product = productService.findProductById(productId);
		
		return new ResponseEntity<Product>(product,HttpStatus.ACCEPTED);
	}
	
}
