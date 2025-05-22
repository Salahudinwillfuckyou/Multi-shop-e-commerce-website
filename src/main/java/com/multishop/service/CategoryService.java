package com.multishop.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multishop.model.Category;
import com.multishop.repository.CategoryRepository;

@Service
public class CategoryService 
{
	@Autowired
	private CategoryRepository catRep;
	
	public Category getCategory(long id) {
		Category category = catRep.findById(id).get();
		return category;
	}
	
	public Category saveCategory(Category category) {
		return catRep.save(category);
	}
	
	public List<Category> findAllRecord() {
		// TODO Auto-generated method stub
		return catRep.findAll();
	}
	
	public Category getCategory(String name) {
		return catRep.findByCategoryName(name);
	}
	
	public List<Category> getTopSixCategories() {
		return catRep.findTopSixCategories();
	}
	
	public Long isChildExists(long id) {
		return catRep.countProductsByCategory(id);
	}
	
	public Map<String, Integer> getCategoryStats() {
        return catRep.findCategorySales();
    }
}
