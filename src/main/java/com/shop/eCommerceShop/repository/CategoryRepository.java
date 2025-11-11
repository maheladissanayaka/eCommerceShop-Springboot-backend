package com.shop.eCommerceShop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shop.eCommerceShop.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{
	
	public Category findByName(String name);
	
	@Query("SELECT c FROM Category c WHERE c.name = :name AND c.parentCategory.name = :parentCategoryName")
	public Category findByNameAndParant(@Param("name")String name, 
			@Param("parantCategoryName")String parantCategoryName);
}
