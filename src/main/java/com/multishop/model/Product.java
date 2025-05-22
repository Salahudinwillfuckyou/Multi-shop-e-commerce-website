package com.multishop.model;


import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@SuppressWarnings("deprecation")
@Entity
@Where(clause = "is_deleted = false")
public class Product 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long productId;
	@Column(columnDefinition = "VARCHAR(50)")
	public String productName;
	@ManyToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name="fk_category_id", referencedColumnName = "categoryId", nullable=false)
	public Category category;
	@Nullable
	public String productImage;
	public double unitPrice;
	@Nullable
	public String description;
	public boolean isAvailable;
	@Column(name = "is_deleted", nullable = false)
    public boolean isDeleted;
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<ProductShop> productShopList = new ArrayList<ProductShop>();
	
	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Product(String productName, Category category, String image, double unitPrice) {
		super();
		this.productName = productName;
		this.category = category;
		this.productImage = image;
		this.unitPrice = unitPrice;
		this.isAvailable = true;
	}

	
	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ProductShop> getProductShopList() {
		return productShopList;
	}

	public void setProductShopList(List<ProductShop> productShopList) {
		this.productShopList = productShopList;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public long getProduct_id() {
		return productId;
	}

	public void setProduct_id(long product_id) {
		this.productId = product_id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getImage() {
		return productImage;
	}

	public void setImage(String image) {
		this.productImage = image;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

}
