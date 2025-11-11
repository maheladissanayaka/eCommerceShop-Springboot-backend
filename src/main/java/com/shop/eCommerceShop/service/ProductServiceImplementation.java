package com.shop.eCommerceShop.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Category;
import com.shop.eCommerceShop.model.Product;
import com.shop.eCommerceShop.repository.CategoryRepository;
import com.shop.eCommerceShop.repository.productRepository;
import com.shop.eCommerceShop.request.CreateProductRequest;

import ch.qos.logback.core.model.processor.ProcessorException;

@Service
public class ProductServiceImplementation implements ProductService{
	
	private productRepository productRepository;
	private UserService userServices;
	private CategoryRepository categoryRepository;
	
	public ProductServiceImplementation(productRepository productRepository,UserService userService,
			CategoryRepository categoryRepository) {
		this.productRepository=productRepository;
		this.userServices=userService;
		this.categoryRepository=categoryRepository;
	}
	
	@Override
	public Product createProduct(CreateProductRequest req) {
		
		Category topLevel=categoryRepository.findByName(req.getTopLevelCategory());
		
		if(topLevel==null) {
			Category topLavelCategory=new Category();
			topLavelCategory.setName(req.getTopLevelCategory());
			topLavelCategory.setLevel(1);
			
			topLevel=categoryRepository.save(topLavelCategory);
		}
		
		Category secondLevel=categoryRepository.
				findByNameAndParant(req.getSecondLevelCategory(), topLevel.getName());
		
		if(secondLevel==null) {
			
			Category secondLavelCategory=new Category();
			secondLavelCategory.setName(req.getSecondLevelCategory());
			secondLavelCategory.setParentCategory(topLevel);
			secondLavelCategory.setLevel(2);
			
			secondLevel=categoryRepository.save(secondLavelCategory);
		}
		
		Category thirdLevel=categoryRepository.findByNameAndParant(req.getThirdLevelCategory(),secondLevel.getName());
		if(thirdLevel==null) {
			Category thirdLavelCategory=new Category();
			thirdLavelCategory.setName(req.getThirdLevelCategory());
			thirdLavelCategory.setParentCategory(secondLevel);
			thirdLavelCategory.setLevel(3);
			
			thirdLevel=categoryRepository.save(thirdLavelCategory);
		}
		
		Product product = new Product();
		product.setTitle(req.getTitle());
		product.setColor(req.getColor());
		product.setDescription(req.getDescription());
		product.setDiscountPrice(req.getDiscountPrice());
		product.setDiscountPersent(req.getDiscountPersent());
		product.setImageUrl(req.getImageUrl());
		product.setBrand(req.getBrand());
		product.setPrice(req.getPrice());
		product.setSizes(req.getSizes());
		product.setQuantity(req.getQuantity());
		product.setCategory(thirdLevel);
		product.setCreatedAt(LocalDateTime.now());
		
		Product savedProduct = productRepository.save(product);
		
		return savedProduct;
	}

	@Override
	public String deleteProduct(int productId) throws HandleException {
		Product product = findProductById(productId);
		product.getSizes().clear();
		productRepository.delete(product);
		return "Product deleted Successfully";
	}

	@Override
	public Product updateProduct(int productId, Product req) throws HandleException {
		Product product = findProductById(productId);
		
		if(req.getQuantity()!=0) {
			product.setQuantity(req.getQuantity());
		}
		return productRepository.save(product);
	}

	@Override
	public Product findProductById(int id) throws HandleException {
		Optional<Product>opt=productRepository.findById(id);
		
		if(opt.isPresent()) {
			return opt.get();
		}
		throw new HandleException("Product not found with id "+id);
	}

	@Override
	public List<Product> findProductByCategory(String category) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Product> getAllProduct(String category, List<String> colors, List<String> sizes, Integer minPrice,
			Integer maxPrice, Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize) {
		
		Pageable pageble = PageRequest.of(pageNumber, pageSize);
		
		List<Product> products = productRepository.filterProducts(category, minPrice, maxPrice, minDiscount, sort);
		
		if(!colors.isEmpty()) {
			products=products.stream().filter(p-> colors.stream().anyMatch(c->c.equalsIgnoreCase(p.getColor())))
					.collect(Collectors.toList()); 
		}
		
		if(stock!=null) {
			if(stock.equals("in_stock")){
				products=products.stream().filter(p->p.getQuantity()>0).collect(Collectors.toList());
			}
			else if(stock.equals("out_of_stock")) {
				products=products.stream().filter(p->p.getQuantity()>0).collect(Collectors.toList());
			}
		}
		
		int startIndex=(int)pageble.getOffset();
		int endIndex=Math.min(startIndex + pageble.getPageSize(), products.size());
		
		List<Product> pageContent = products.subList(startIndex, endIndex);
		
		Page<Product>filterdProducts=new PageImpl<>(pageContent,pageble,products.size());
		return filterdProducts;
	}

	@Override
	public List<Product> findAllProduct() throws HandleException {
		List<Product> products = productRepository.findAll();
		if (products.isEmpty()) {
			throw new HandleException("No products found");
		}
		return products;
	}

}
