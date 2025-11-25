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
		
		Category category = null;
		
		// Handle category hierarchy - use the first non-empty category level
		if(req.getTopLevelCategory() != null && !req.getTopLevelCategory().trim().isEmpty()) {
			Category topLevel = categoryRepository.findByName(req.getTopLevelCategory());
			
			if(topLevel == null) {
				Category topLavelCategory = new Category();
				topLavelCategory.setName(req.getTopLevelCategory());
				topLavelCategory.setLevel(1);
				topLevel = categoryRepository.save(topLavelCategory);
			}
			
			if(req.getSecondLevelCategory() != null && !req.getSecondLevelCategory().trim().isEmpty()) {
				Category secondLevel = categoryRepository.findByNameAndParant(req.getSecondLevelCategory(), topLevel.getName());
				
				if(secondLevel == null) {
					Category secondLavelCategory = new Category();
					secondLavelCategory.setName(req.getSecondLevelCategory());
					secondLavelCategory.setParentCategory(topLevel);
					secondLavelCategory.setLevel(2);
					secondLevel = categoryRepository.save(secondLavelCategory);
				}
				
				if(req.getThirdLevelCategory() != null && !req.getThirdLevelCategory().trim().isEmpty()) {
					Category thirdLevel = categoryRepository.findByNameAndParant(req.getThirdLevelCategory(), secondLevel.getName());
					if(thirdLevel == null) {
						Category thirdLavelCategory = new Category();
						thirdLavelCategory.setName(req.getThirdLevelCategory());
						thirdLavelCategory.setParentCategory(secondLevel);
						thirdLavelCategory.setLevel(3);
						thirdLevel = categoryRepository.save(thirdLavelCategory);
					}
					category = thirdLevel;
				} else {
					category = secondLevel;
				}
			} else {
				category = topLevel;
			}
		}
		
		// If no category provided, create a default "Uncategorized" category
		if(category == null) {
			Category defaultCategory = categoryRepository.findByName("Uncategorized");
			if(defaultCategory == null) {
				defaultCategory = new Category();
				defaultCategory.setName("Uncategorized");
				defaultCategory.setLevel(1);
				category = categoryRepository.save(defaultCategory);
			} else {
				category = defaultCategory;
			}
		}
		
		Product product = new Product();
		product.setTitle(req.getTitle());
		product.setColor(req.getColor());
		product.setDescription(req.getDescription());
		product.setImageUrl(req.getImageUrl());
		product.setBrand(req.getBrand());
		product.setPrice(req.getPrice());
		product.setSizes(req.getSizes());
		product.setQuantity(req.getQuantity());
		product.setCategory(category);
		product.setCreatedAt(LocalDateTime.now());
		
		// Calculate discountPrice from discountPersent if provided
		if (req.getDiscountPersent() > 0) {
			product.setDiscountPersent(req.getDiscountPersent());
			double discountPrice = req.getPrice() * (1 - req.getDiscountPersent() / 100.0);
			product.setDiscountPrice(discountPrice);
		} else {
			// If no discount percentage, use the provided discountPrice or set to price
			product.setDiscountPersent(0);
			product.setDiscountPrice(req.getDiscountPrice() > 0 ? req.getDiscountPrice() : req.getPrice());
		}
		
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
		
		if(req.getTitle() != null && !req.getTitle().trim().isEmpty()) {
			product.setTitle(req.getTitle());
		}
		if(req.getDescription() != null) {
			product.setDescription(req.getDescription());
		}
		if(req.getPrice() != 0) {
			product.setPrice(req.getPrice());
		}
		// Calculate discountPrice from discountPersent if provided
		if(req.getDiscountPersent() > 0) {
			product.setDiscountPersent(req.getDiscountPersent());
			double discountPrice = product.getPrice() * (1 - req.getDiscountPersent() / 100.0);
			product.setDiscountPrice(discountPrice);
		} else if(req.getDiscountPrice() > 0) {
			// If discountPrice is provided but no discountPersent, calculate the percentage
			product.setDiscountPrice(req.getDiscountPrice());
			if(product.getPrice() > 0) {
				double discountPercent = ((product.getPrice() - req.getDiscountPrice()) / product.getPrice()) * 100.0;
				product.setDiscountPersent(discountPercent);
			}
		}
		if(req.getQuantity() != 0) {
			product.setQuantity(req.getQuantity());
		}
		if(req.getBrand() != null && !req.getBrand().trim().isEmpty()) {
			product.setBrand(req.getBrand());
		}
		if(req.getColor() != null) {
			product.setColor(req.getColor());
		}
		if(req.getImageUrl() != null && !req.getImageUrl().trim().isEmpty()) {
			product.setImageUrl(req.getImageUrl());
		}
		if(req.getSizes() != null && !req.getSizes().isEmpty()) {
			product.setSizes(req.getSizes());
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
		
		if(colors != null && !colors.isEmpty()) {
			products=products.stream().filter(p-> colors.stream().anyMatch(c->c.equalsIgnoreCase(p.getColor())))
					.collect(Collectors.toList()); 
		}
		
		if(sizes != null && !sizes.isEmpty()) {
			products=products.stream().filter(p-> {
				if(p.getSizes() == null || p.getSizes().isEmpty()) return false;
				return p.getSizes().stream().anyMatch(s-> {
					String sizeName = s.getName() != null ? s.getName() : s.toString();
					return sizes.stream().anyMatch(size->size.equalsIgnoreCase(sizeName));
				});
			}).collect(Collectors.toList());
		}
		
		if(stock!=null) {
			if(stock.equals("in_stock")){
				products=products.stream().filter(p->p.getQuantity()>0).collect(Collectors.toList());
			}
			else if(stock.equals("out_of_stock")) {
				products=products.stream().filter(p->p.getQuantity()==0).collect(Collectors.toList());
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
