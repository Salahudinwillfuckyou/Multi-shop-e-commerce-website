package com.multishop.model;


import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Where;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@SuppressWarnings("deprecation")
@Entity
@Where(clause = "is_deleted = false")
public class Category 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long categoryId;
	
	@Column(columnDefinition = "VARCHAR(30)")
	public String categoryName;
	
	@Column(name = "is_deleted", nullable = false)
    public boolean isDeleted;
	
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	public List<Product> products = new ArrayList<Product>();
	
	public Category() 
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Category(String categoryName, List<Product> products) {
		super();
		this.categoryName = categoryName;
		this.products = products;
	}

	public long getCategoryId() {
	    return this.categoryId;
	}

	public void setCategoryId(long category_id) {
		this.categoryId = category_id;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String category_name) {
		this.categoryName = category_name;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
}
