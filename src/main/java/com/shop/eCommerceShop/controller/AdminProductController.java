package com.shop.eCommerceShop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Product;
import com.shop.eCommerceShop.request.CreateProductRequest;
import com.shop.eCommerceShop.responce.ApiResponse;
import com.shop.eCommerceShop.service.ProductService;

@RestController
@RequestMapping("/api/admin/Products")
public class AdminProductController {
	
	@Autowired
	public ProductService productService;
	
	@PostMapping
	public ResponseEntity<Product>createProduct(@RequestBody CreateProductRequest req){
		Product product = productService.createProduct(req);
		return new ResponseEntity<Product>(product,HttpStatus.CREATED);
	}
	
	@DeleteMapping("/{productId}/delete")
	public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Integer productId)throws HandleException{
		
		productService.deleteProduct(productId);
		ApiResponse res = new ApiResponse();
		res.setMessage("product deleted successfully");
		res.setStatus(true);
		return new ResponseEntity<>(res,HttpStatus.OK);
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<Product>>findAllProduct() throws HandleException{
		
		List<Product>products=productService.findAllProduct();
		
		return new ResponseEntity<>(products,HttpStatus.OK);
	}
	
	@PutMapping("/{productId}/update")
	public ResponseEntity<Product>updateProduct(@RequestBody Product req,@PathVariable Integer productId)throws HandleException{
		
		Product product =productService.updateProduct(productId, req);
		return new ResponseEntity<Product>(product,HttpStatus.CREATED);
	}
	
	@PostMapping("/creates")
	public ResponseEntity<ApiResponse>createMultipleProduct(@RequestBody CreateProductRequest[] req){
		
		for(CreateProductRequest product:req) {
			productService.createProduct(product);
		}
		ApiResponse res = new ApiResponse();
		res.setMessage("product Created successfully");
		res.setStatus(true);
		
		return new ResponseEntity<>(res,HttpStatus.CREATED);
	}

}
